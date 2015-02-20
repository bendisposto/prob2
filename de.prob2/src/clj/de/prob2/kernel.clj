(ns de.prob2.kernel
  (:require [com.stuartsierra.component :as component])
  (:import de.prob.Main
           (de.prob.statespace AnimationSelector Trace IModelChangedListener IAnimationChangeListener StateSpace)))


(defn notify-model-changed [{:keys [clients send-fn!] :as sente} state-space]
  (doseq [c (:any @clients)] (send-fn! c [::model-changed {:space :dude}])))
(defn notify-trace-changed [send-fn trace current?])
(defn notify-animator-busy [busy?])


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
          (animatorStatus [this busy] (notify-animator-busy busy)))]
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
