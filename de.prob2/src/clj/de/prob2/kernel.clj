(ns de.prob2.kernel
  (:require [com.stuartsierra.component :as component])
  (:import de.prob.Main
           (de.prob.statespace AnimationSelector Trace IModelChangedListener IAnimationChangeListener StateSpace)))


(defn instantiate [{inj :injector :as prob} cls]
  (.getInstance inj cls))

(defn- install-handlers [injector]
  (let [animations (.getInstance injector AnimationSelector)
        listener
        (reify
          IModelChangedListener
          (modelChanged [this state-space] (println :model-changed state-space))
          IAnimationChangeListener
          (traceChange [this trace current] (println :trace-changed trace :current current))
          (animatorStatus [this busy] (println :animator-busy busy)))]
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
                listener (install-handlers injector)
                _ (println " -> Installed Listeners")]
            (assoc this :injector injector :listener listener)))))
  (stop [this]
    (if injector (do (println "Shutting down ProB 2.0")
                     (dissoc this :injector :listener))
        this)))

(defn prob []
  (component/using (map->ProB {}) [:sente]))


