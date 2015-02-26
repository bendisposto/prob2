(ns de.prob2.kernel
  (:require [com.stuartsierra.component :as component]
            [de.prob2.handler :as handler])
  (:import de.prob.Main
           (de.prob.statespace AnimationSelector Trace IModelChangedListener IAnimationChangeListener StateSpace)))


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
        parameters (.getParameterPredicate transition)
        return-values (.getReturnValues transition)]
    {:name name
     :id id
     :parameters parameters
     :return-values (into [] return-values)}))

;; TraceElement: previousTE  (-evt-> state)
(defn append-trace-element [ts te]
  (let [src (transform-state (.getSrc te))
        dest (transform-state (.getDest te))
        t (.getTransition te)
        trans (when t (transform-transition t))]
    (conj ts {:src src :dest dest :trans trans})))

(defn prepare-trace [trace]
  (loop [te (.getCurrent trace)
         ts []]
    (let [pr (.getPrevious te)]
      (if-not pr
        ts
        (recur pr (append-trace-element ts te))))))

;; FIXME We should only send information to clients who actually care
(defn notify-model-changed [{:keys [clients] :as sente} state-space]
  (doseq [c (:any @clients)]
    (handler/send! sente c ::model-changed (extractE (.getModel state-space)))))

;; FIXME We should only send information to clients who actually care
(defn notify-trace-changed [{:keys [clients] :as sente} trace current?]
  (doseq [c (:any @clients)]
    (handler/send!
     sente c
     ::trace-changed
     {:trace-id (.getProperty trace "UUID")
      :trace (prepare-trace trace)})))

;; FIXME We should only send information to clients who actually care
(defn notify-animator-busy [{:keys [clients] :as sente} busy?]
  (doseq [c (:any @clients)]
    (handler/send! sente c (if busy? ::animator-is-busy ::animator-is-idle) {})))



(defn instantiate [{inj :injector :as prob} cls]
  (.getInstance inj cls))

(defn- install-handlers [sente injector]
  (let [animations (.getInstance injector AnimationSelector)
        listener
        (reify
          IModelChangedListener
          (modelChanged [this state-space] (notify-model-changed sente state-space))
          IAnimationChangeListener
          (traceChange [this trace current] (notify-trace-changed sente trace current))
          (animatorStatus [this busy] (notify-animator-busy sente busy)))]
    (.registerAnimationChangeListener animations listener)
    (.registerModelChangedListener animations listener)
    listener))

(defrecord ProB [injector listener sente]
  component/Lifecycle
  (start [this]
    (if injector
      this
      (do (println "Preparing ProB 2.0 Kernel")
          (let [injector (Main/getInjector)
                _ (println " -> Got the injector")
                listener (install-handlers sente injector)
                _ (println " -> Installed Listeners")]
            (assoc this :injector injector :listener listener)))))
  (stop [this]
    (if injector (do (println "Shutting down ProB 2.0")
                     (dissoc this :injector :listener))
        this)))

(defn prob []
  (component/using (map->ProB {}) [:sente]))
