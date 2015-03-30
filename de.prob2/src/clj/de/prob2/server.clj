(ns de.prob2.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as http-kit])
  (:gen-class))


(defrecord WebServer [handler ip port stop-fn]
  component/Lifecycle
  (start [this]
    (if stop-fn this
        (let [stop-fn (http-kit/run-server (:handler handler) {:ip ip :port port})
              port' (-> stop-fn meta :local-port)
              this' (assoc this
                      :ip ip     
                      :port port'
                      :stop-fn (fn [] (stop-fn :timeout 100)))]
          (println "Started server on " (str ip ":" port'))
          this')))
  (stop [this]
    (if stop-fn
      (do
        (stop-fn)
        (println "Stopped server.")
        (dissoc this :stop-fn))
      this)))

(defn server [ip port]
  (let [ip (or ip "127.0.0.1")]
    (component/using (map->WebServer {:ip ip :port port}) [:handler])))
