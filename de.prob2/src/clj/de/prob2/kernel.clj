(ns de.prob2.kernel
  (:require [com.stuartsierra.component :as component]
            [de.prob2.sente :as snt]
            [clojure.reflect :as refl])
  (:import de.prob.Main
           (de.prob.statespace AnimationSelector Trace ITraceChangesListener StateSpace)))


(defn kebap-case
  ([cls] (kebap-case cls ""))
  ([cls add]
     (keyword
      (str (clojure.string/join
            "-"
            (map (fn [s] (.toLowerCase s))
                 (map second (re-seq #"([A-Z][a-z]*)" (.getSimpleName cls))))) add))))

(defn methods [e]
  (into #{} (map :name (:members (refl/reflect e)))))

(defn default-map [v kids]
  (merge {:name (.getName v)} kids))

(defn formula-map [formula]
  {:formula (.getCode formula)
   :formula-id (.getUUID (.getFormulaId formula))})

(defn formula-element [e]
  (let [m (methods e)
        mmap (if (m 'getName) {:label (.getName e)} {})
        formula (.getFormula e)]
    (into mmap (formula-map formula))))

(defn theorem?-element [e theorem?]
  (merge (formula-element e) {:theorem? theorem?}))

(defn clean-up-machine [kids]
  (let [invs (:invariants kids)
        assertions (:assertions kids)
        kids' (dissoc kids :assertions)]
    (if assertions
      (assoc kids' :invariants (concat assertions invs))
      kids')))

(defn clean-up-event [kids]
  (let [events (:events kids)]
    (assoc (dissoc kids :events) :refines (map :name events))))

(defprotocol Transform (transform [this kids]))
(extend-protocol Transform
  de.prob.model.representation.Machine
  (transform [v kids] (default-map v (clean-up-machine kids)))
  de.prob.model.eventb.Context
  (transform [v kids] (default-map v kids))
  de.prob.model.eventb.EventBGuard
  (transform [v _] (theorem?-element v (.isTheorem v)))
  de.prob.model.representation.AbstractTheoremElement
  (transform [v _] (theorem?-element v (.isTheorem v)))
  de.prob.model.representation.AbstractFormulaElement
  (transform [v _] (formula-element v))
  de.prob.model.classicalb.Operation
  (transform [v kids] (merge kids {:name (.getName v) :return-values (.getProperty v "output") :parameters (.getProperty v "parameters")}))
  de.prob.model.eventb.Event
  (transform [v kids] (merge (clean-up-event kids)
                             {:name (.getName v)
                              :kind (keyword (.toLowerCase (str (.getType v))))}))
  de.prob.model.representation.Action
  (transform [v _] (.getCode (.getCode v)))
  de.prob.model.eventb.EventParameter
  (transform [v _] (.getName v))
  de.prob.model.eventb.Witness
  (transform [v _] (.getCode (.getFormula v)))
  java.lang.Object
  (transform [o k] {:type :unknown :class (type o) :object (.toString o) :kids k}))

(defn name-keys [cls]
  (let [name (.. cls getSimpleName toLowerCase)]
    (condp = name
      "property" :properties
      "bevent"   :events
      "witness"  :witnesses
      "eventparameter" :parameters
      "variant"  :variant
      name       (keyword (str name "s")))))

(def exclude #{de.prob.model.eventb.ProofObligation
               de.prob.model.eventb.Context
               de.prob.model.representation.Machine})

(declare extractE)

(defn extractV [x]
  (let [c (.getKey x) lv (.getValue x)]
    (if (exclude c)
      []
      [(name-keys c) (map extractE lv)])))

(defn extractE [absel]
  (let [chd (.getChildren absel)]
    (if (seq chd)
      (transform absel (into {} (remove empty? (map extractV chd))))
      (transform absel nil))))

(defn extract-edge [edge]
  (let [from (.getElementName (.getFrom edge))
        to   (.getElementName (.getTo edge))
        type (keyword (.toLowerCase (str (.getRelationship edge))))]
    {:from from :to to :type type}))

(defn extract-dep-graph [model]
  (let [graph (.getGraph model)]
    (map extract-edge (.getEdges graph))))

(defn extract-model [model]
  {:dir (.getModelDirPath model)
   :main-component-name (.getName (.getMainComponent model))
   :filename (.getAbsolutePath (.getModelFile model))
   :type (kebap-case (class model))
   :dependency-graph (extract-dep-graph model)
   :components (into {} (map (fn [e] [(.getKey e) (extractE (.getValue e))]) (.getComponents model)))})

(defn transform-state-values [initialized? values]
  (into {}
        (map (fn [x] [(.toString (.getKey x))
                      (if initialized? (.toString (.getValue x)) "not initialized" )]) values)))

(defn transform-state [state]
  (println state)
  {:initialized? (.isInitialised state)
   :inv-ok? (.isInvariantOk state)
   :timeout? (.isTimeoutOccurred state)
   :max-trans? (.isMaxTransitionsCalculated state)
   :id (.getId state)
   :state-errors (into [] (.getStateErrors state))
   :transitions-with-timeout (into #{} (.getTransitionsWithTimeout state))
   :values (transform-state-values (.isInitialised state) (.getValues state))})


(defn transform-transition [transition]
  (let [name (.getName transition)
        id (.getId transition)
        parameters (.getParams transition)
        return-values (.getReturnValues transition)]
    {:name name
     :id id
     :parameters parameters
     :return-values (into [] return-values)}))

(defn prepare-trace-element [te]

  (let [s (.getSrc te)
        src (transform-state s)
        d (.getDest te)
        dest (transform-state (if d d s))]
    {:previous src :current dest}))


(defn prepare-trace-packet [trace]
  (let [h (.getTransitionList trace true)
        history (map transform-transition h)
        te (.getCurrent trace)
        cur (prepare-trace-element te)
        uuid (.getUUID trace)
        cur-index (.getIndex te)
        model (extract-model (.getModel trace))
        animator-id (.getId (.getStateSpace trace))
        ]

    [uuid (assoc cur :trace-id uuid :history history :current-index cur-index :model model :animator-id animator-id)]))


(defn prepare-ui-state-packet [traces]
  {:traces (into {} (mapv prepare-trace-packet traces))})


;; FIXME We should only send information to clients who actually care
(defn notify-trace-changed [{:keys [clients] :as sente} traces]
  (let [packet (prepare-ui-state-packet traces)]
    (doseq [c (:any @clients)]
      (snt/send!
       sente c
       ::ui-state
       packet))))

(defn notify-trace-removed [{:keys [clients] :as sente} traces]
  (doseq [c (:any @clients)]
    (snt/send!
     sente c
     ::trace-removed
     traces)))


(defn instantiate [{inj :injector :as prob} cls]
  (.getInstance inj cls))

(defn- install-handlers [sente animations]
  (let [listener
        (reify
          ITraceChangesListener
          (changed [this traces] (notify-trace-changed sente traces))
          (removed [this traces] (do (println :remove traces) (notify-trace-removed sente traces)))
          (animatorStatus [this busy] (println "animation status")))]
    (.registerAnimationChangeListener animations listener)
    listener))

(defmulti dispatch-kernel snt/extract-action)
(defmethod dispatch-kernel :handshake [{:keys [animations sente]} a]
  (let [traces (.getTraces animations)
        packet (prepare-ui-state-packet traces)
        client (get-in a [:ring-req :session :uid])]
    (snt/send!
     sente client
     ::ui-state
     packet)))

(defmethod dispatch-kernel :kill! [{:keys [animations]} a]
  (let [trace-ids (get-in a [:?data :trace-ids])
        traces (mapv #(.getTrace animations %) trace-ids)
        animators (into #{} (mapv #(.getStateSpace %) traces))]
    (doseq [t traces] (.removeTrace animations t))
    (doseq [a animators] (.kill a))))


(defrecord ProB [injector listener sente animations]
  component/Lifecycle
  (start [this]
    (if injector
      this
      (do (println "Preparing ProB 2.0 Kernel")
          (let [injector (Main/getInjector)
                _ (println " -> Got the injector")
                animations (.getInstance injector de.prob.statespace.Animations)
                _ (println " -> got Animations object")
                listener (install-handlers sente animations)
                _ (println " -> Installed Listeners")
                this' (assoc this :injector injector :listener listener :animations animations)]
            (defmethod snt/handle-updates :prob2 [_ a] (dispatch-kernel this' a))
            this'))))
  (stop [{:keys [animations listener] :as this}]
    (if injector (do (println "Shutting down ProB 2.0")
                     (let [traces (.getTraces animations)
                           animators (into #{} (mapv #(.getStateSpace %) traces))]
                       (doseq [t traces]
                         (println "  * Removing " (str (.getProperty t "UUID")))
                         (.removeTrace animations t))
                       (doseq [a animators]
                         (println "  * Killing " a)
                         (.kill a)))
                     (println " * Deregistering Listener")
                     (.deregisterAnimationChangeListener animations listener)
                     (dissoc this :injector :listener :animations :sente))
        this)))

(defn prob []
  (component/using (map->ProB {}) [:sente]))
