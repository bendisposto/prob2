(ns de.prob2.system
  (:require [de.prob2.server :as server]
            [de.prob2.handler :as handler]
            [de.prob2.kernel :as kernel]
            [com.stuartsierra.component :as component]))

(defn mk-system [config-options]
  (let [{:keys [port]} config-options]
    (component/system-map
     :server (server/server port)
     :sente (handler/mk-sente)
     :handler (handler/mk-handler)
     :routes (handler/mk-routes)
     :prob (kernel/prob)
     )))
