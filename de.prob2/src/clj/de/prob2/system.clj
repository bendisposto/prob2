(ns de.prob2.system
	(:require [de.prob2.server :as server]
            [de.prob2.handler :as handler]
		      [com.stuartsierra.component :as component]))

(defn mk-system [config-options]
  (let [{:keys [port routes]} config-options]
    (component/system-map
     :server (server/server port)
     :sente (handler/mk-sente)
     :handler (handler/mk-handler routes))))
