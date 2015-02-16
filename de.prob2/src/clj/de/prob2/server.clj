(ns de.prob2.server
  (:require [de.prob2.handler :refer [app]]
            [org.httpkit.server :as http-kit])
  (:gen-class))


(defn start-web-server! [ring-handler port]
  (println "Starting http-kit...")
  (let [http-kit-stop-fn (http-kit/run-server ring-handler {:port port})]
    {:server  nil ; http-kit doesn't expose this
     :port    (:local-port (meta http-kit-stop-fn))
     :stop-fn (fn [] (http-kit-stop-fn :timeout 100))}))


 (defn -main [& args]
   (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
     (start-web-server! app port)))
