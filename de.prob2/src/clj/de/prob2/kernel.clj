(ns de.prob2.kernel
  (:require [com.stuartsierra.component :as component]
            [de.prob2.sente :as snt])
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

(defn default-map [v m]
  (into  {:type (kebap-case (class v)) :name (.toString v)} m))


(defprotocol Transform (transform [this]))
(extend-protocol Transform
  de.prob.model.representation.AbstractModel
  (transform [v] {:type (kebap-case (class v)) :dir (.getModelDirPath v) :file (.getAbsolutePath (.getModelFile v)) :main-component-name (.toString (.getMainComponent v))})
  de.prob.model.representation.Machine
  (transform [v] (default-map v {}))
  de.prob.model.eventb.Context
  (transform [v] (default-map v {}))
  de.prob.model.representation.BSet
  (transform [v] (default-map v {}))
  de.prob.model.representation.AbstractFormulaElement
  (transform [v] (default-map v {:formula-id (.. v getFormula getFormulaId getUUID)}))
  de.prob.model.classicalb.Operation
  (transform [v] {:type (kebap-case (class v)) :name (.getName v) :output (.getProperty v "output") :parameter (.getProperty v "parameters")})
  de.prob.model.eventb.Event
  (transform [v] {:type (kebap-case (class v)) :name (.getName v)})
  de.prob.model.representation.Action
  (transform [v] (default-map v {}))
  java.lang.Object
  (transform [o] {:type :unknown :class (type o) :object (.toString o)}))

(def exclude #{de.prob.model.eventb.ProofObligation})

(declare extractE)

(defn extractV [x]
  (let [c (.getKey x) lv (.getValue x)]
    [(keyword (.. c getSimpleName toLowerCase)) (if (exclude c) [] (map extractE lv))]))

(defn extractE [absel]
  (let [chd (.getChildren absel)
        tgt (transform absel)]
    (if (seq chd)
      (do (->> chd (map extractV) (into tgt)))
      tgt)))

(defn transform-state-values [initialized? values]
  (into {} (map (fn [x] [(.toString (.getKey x)) (if initialized? (.toString (.getValue x)) "not initialized" )]) values)))

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
        model (extractE (.getModel trace))
        model-id (.getId (.getStateSpace trace))
        ]

    (assoc cur :trace-id uuid :history history :current-index cur-index :model {:data model :model-id model-id})))


;; FIXME We should only send information to clients who actually care
(defn notify-model-changed [{:keys [clients] :as sente} state-space]
  (doseq [c (:any @clients)]
    (snt/send! sente c ::model-changed (extractE (.getModel state-space)))))

;; FIXME We should only send information to clients who actually care
(defn notify-trace-changed [{:keys [clients] :as sente} traces]
  (let [packet (mapv prepare-trace-packet traces)]
    (doseq [c (:any @clients)]
      (snt/send!
       sente c
       ::trace-changed
       packet))))

;; FIXME We should only send information to clients who actually care
(defn notify-animator-busy [{:keys [clients] :as sente} busy?]
  (doseq [c (:any @clients)]
    (snt/send! sente c (if busy? ::animator-is-busy ::animator-is-idle) {})))



(defn instantiate [{inj :injector :as prob} cls]
  (.getInstance inj cls))

(defn- install-handlers [sente animations]
  (let [listener
        (reify
          ITraceChangesListener
          (changed [this traces] (notify-trace-changed sente traces))
          (removed [this traces] (println "removed"))
          (animatorStatus [this busy] (println "animation status")))]
    (.registerAnimationChangeListener animations listener)
    listener))

(defn trace-list [trace]
  (let [uuid (.getUUID trace)
        model (transform (.getModel trace))
        animator-id (.getId (.getStateSpace trace))]
    (into model  {:uuid uuid :animator-id animator-id})))

(defmulti dispatch-kernel snt/extract-action)
(defmethod dispatch-kernel :handshake [{:keys [animations sente]} a]
  (let [traces (.getTraces animations)
        packet (mapv trace-list traces)
        client (get-in a [:ring-req :session :uid])]
    (snt/send!
     sente client
     ::traces
     packet)))

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
                     (doseq [t (.getTraces animations)]
                       (println "  * Removing " (str (.getProperty t "UUID")))
                       (.removeTrace animations t))
                     (println " * Deregistering Listener")
                     (.deregisterAnimationChangeListener animations listener)
                     (dissoc this :injector :listener :animations :sente))
        this)))

(defn prob []
  (component/using (map->ProB {}) [:sente]))
