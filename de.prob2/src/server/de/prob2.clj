(ns de.prob2
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [org.httpkit.server :as http-kit-server]
            [com.stuartsierra.component :as component]
            [prob-ui.state-store :as sync])
  (:import [de.prob Main]
           [de.prob.scripting ScriptEngineProvider]))


(def sys (atom []))

(defn bar [app request]
  (println :app app :request request)
  "ok")

(defn setup-routes [app]
  (routes
   (GET "/bar" [request]
        (bar app request))
   (GET "/updates/:id" [id]
        (sync/delta id))
   (route/resources "/")
   (route/not-found "<h1>404 Page not found</h1>")))

(defn system-middleware [ring-handler]
  (fn [request]
    (ring-handler
     (assoc request
       ::app (::app @sys)))))


(defrecord WebServer [app port ip]
  component/Lifecycle
  (start [this]
    (print "Starting Web Server ... ")
    (let [s (http-kit-server/run-server (setup-routes this) {:port port :ip ip})
          p (:local-port (meta s))]
      (println "done. Listening on Port" p)
      (assoc this :webserver s)))
  (stop [this]
    (print "Shutting down Web Server ... ")
    (let [s (:webserver this)]
      (s :timeout 500)
      (println "done.")
      (dissoc this :webserver))))

(defrecord App []
  component/Lifecycle
  (start [this] (println "Starting App") (assoc this :app "app"))
  (stop [this]  (println "Stopping App") (dissoc this :app)))

(defn new-webserver [port ip]
  (map->WebServer {:port port :ip ip}))

(defn new-app [] (map->App {}))

(defn new-system [{:keys [port ip] :or {port 0 ip "localhost"}}]
  (component/system-map
   :webserver (component/using (new-webserver port ip) [:app])
   :app (new-app)
   :sync-store (sync/new-syncstore)))


(def injector (Main/getInjector))

(defn get-instance [c]
  (.getInstance injector c))

(def groovy-instance
  (let [sep (get-instance ScriptEngineProvider)]
    (.get sep)))

(defn groovy [input]
  (let [c (StringBuffer.)]))
