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

(defn retrieve-methods [e]
  (into #{} (map :name (:members (refl/reflect e)))))

(defn default-map [v kids]
  (merge {:name (.getName v)} kids))

(defn formula-map [formula]
  {:formula (.getCode formula)
   :formula-id (.getUUID (.getFormulaId formula))})

(defn formula-element [e]
  (let [m (retrieve-methods e)
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
   :components (into {} (map (fn [e] [(.getKey e) (extractE (.getValue e))])
                             (.getComponents model)))})

(defn extract-transition [transition]
  (when transition
    (let [name (.getName transition)
          id (.getId transition)
          parameters (.getParams transition)
          return-values (.getReturnValues transition)
          src (.getId (.getSource transition))
          dest (.getId (.getDestination transition))
          anim-id (.getId (.stateSpace transition))]
      {:name name
       :id id
       :parameters parameters
       :return-values (into [] return-values)
       :src {:model anim-id :state src}
       :dst {:model anim-id :state dest}})))

(defn extract-trace [trace]
  (let [trace-id (.getUUID trace)
        t (.getTransitionList trace true)
        transitions (map extract-transition t)
        current-index (.getIndex (.getCurrent trace))
        current-transition (extract-transition (.getCurrentTransition trace))
        out-trans (map extract-transition (.getNextTransitions trace true))
        back? (.canGoBack trace)
        forward? (.canGoForward trace)
        model (.getId (.getStateSpace trace))
        current-state {:model model :state (.getId (.getCurrentState trace))}]
    {:trace-id trace-id  :transitions transitions :current-index current-index
     :out-transitions out-trans :back? back? :forward? forward? :model model :current-state current-state :current-transition current-transition}))

(defn extract-state-error [se]
  (let [event (.getEvent se)
        short-desc (.getShortDescription se)
        long-desc (.getLongDescription se)]
    {:event event :short-desc short-desc :long-desc long-desc}))

(defn extract-values [state]
  (let [values (.getValues state)
        value-map (into {} (map (fn [[x y]]
                                  [(.getUUID (.getFormulaId x)) (.getId y)])
                                values))
        result-map (into {} (map (fn [[_ y]]
                                   [(.getId y) (.toString y)])
                                 values))]
    {:values value-map :results result-map}))

(defn extract-state [state]
  (let [id           {:model (.getId (.getStateSpace state))
                      :state (.getId state)}
        initialized? (.isInitialised state)
        inv-ok?      (.isInvariantOk state)
        timeout?     (.isTimeoutOccurred state)
        max-transitions-reached? (.isMaxTransitionsCalculated state)
        state-errors (map extract-state-error (.getStateErrors state))
        events-with-timeout (into [] (.getTransitionsWithTimeout state))
        vals (extract-values state)]
    {:state {:values (:values vals)
             :initialized? initialized?
             :inv-ok? inv-ok?
             :timeout? timeout?
             :max-transitions-reached? max-transitions-reached?
             :id id
             :state-errors state-errors
             :events-with-timeout events-with-timeout}
     :results (:results vals)}))

(defn find-and-extract [{:keys [state]} state-space]
  (extract-state (.getState state-space state)))

(defn prepare-trace [t]
  (let [trace (extract-trace t)
        transitions (concat (:transitions trace) (:out-transitions trace))
        state-space (.getStateSpace t)
        ss (into #{} (concat (map :src transitions) (map :dst transitions)))
        extracted (map (fn [s] (find-and-extract s state-space)) ss)
        states (map :state extracted)
        results (map :results extracted)]
    {:trace trace
     :states (into {} (map (fn [s] [(:id s) s]) states))
     :results (apply merge results)}))

(defn prepare-ui-state-packet [trace-list]
  (let [ms (into #{} (map (fn [t] (.getModel t))) trace-list)
        models (into {} (map (fn [m]
                               [(.getId (.getStateSpace m))
                                (extract-model m)]) ms))
        ts (map prepare-trace trace-list)
        traces (into {} (map (fn [{:keys [trace]}] [(:trace-id trace) trace]) ts))
        states (apply merge (map :states ts))
        results (apply merge (map :results ts))]
    {:traces traces :models models :states states :results results}))



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
