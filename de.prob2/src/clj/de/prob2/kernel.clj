(ns de.prob2.kernel
  (:require [com.stuartsierra.component :as component])
  (:import de.prob.Main))



(defrecord ProB [injector]
  component/Lifecycle
  (start [this]
    (if injector
      this
      (do (println "Preparing ProB 2.0 Kernel")
          (assoc this :injector (Main/getInjector)))))
  (stop [this]
    (if injector (do (println "Shutting down ProB 2.0")
                     (dissoc this :injector))
        this)))

(defn prob []
  (map->ProB {}))

(defn instantiate [{inj :injector} cls]
  (.getInstance inj cls))

