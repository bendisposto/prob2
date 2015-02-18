(ns de.prob2.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as http-kit])
  (:gen-class))


(defrecord WebServer [handler port stop-fn]
  component/Lifecycle
  (start [this]
    (if stop-fn this
        (let [stop-fn (http-kit/run-server handler {:port port})
              port' (-> stop-fn meta :local-port)
              this' (assoc this
                      :port port'
                      :stop-fn (fn [] (stop-fn :timeout 100)))]
          (println "Started server on port " port')
          this')))
  (stop [this]
    (if stop-fn
      (do
        (stop-fn)
        (println "Stopped server.")
        (dissoc this :stop-fn))
      this)))

(defn server [port]
  (component/using (map->WebServer {:port port}) [:handler]))
