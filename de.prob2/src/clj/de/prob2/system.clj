(ns de.prob2.system
	(:require [de.prob2.server :as server]
		      [com.stuartsierra.component :as component]))

(defn mk-system [config-options]
  (let [{:keys [port]} config-options]
    (component/system-map
      :server (server/server port))))