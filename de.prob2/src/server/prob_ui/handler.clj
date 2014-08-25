(ns prob-ui.handler
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.response :as response]
            [prob-ui.state-store :as sync])
  (import [de.prob Main] [de.prob.scripting ScriptEngineProvider]))


(defonce server (atom {}))
(def injector (Main/getInjector))

(defn get-instance [c]
  (.getInstance injector c))

(def groovy-instance
  (let [sep (get-instance ScriptEngineProvider)]
    (.get sep)))

(defn groovy [input]
  (let [c (StringBuffer.)]
    (try
      (let [[result output] (.eval groovy-instance input c)]
        {:result (str result) :output output}
        )
      (catch Exception e {:result nil :output "" :error (str e)}))))


(defroutes main-routes
  (GET "/updates/:id" [id] (sync/delta id))
  (POST "/groovy" {params :params} (str  (groovy (:input params)) "\n"))
  (GET "x" [] "xxx")
  (route/resources "/")
  (route/not-found "Page not found"))


(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))

(defn start-server [port]
  (let [s (run-jetty #'app {:port port :join? false})]
    (swap! server assoc :server s)))
 
