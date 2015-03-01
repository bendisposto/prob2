(ns de.prob2.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [selmer.parser :refer [render-file]]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async :refer (<! <!! >! >!! put! chan go go-loop)]
            [prone.middleware :refer [wrap-exceptions]]
            [environ.core :refer [env]]
            [de.prob2.sente :as snt]
            [de.prob2.stateview :as sv]
            [de.prob2.kernel :as kernel]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream))


(defn default-routes []
  (fn [{:keys [ws-handshake post]} prob]
    [(GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))
     (GET  "/updates" req (ws-handshake req))
     (GET "/stateview/:trace" [trace] (sv/create-state-view prob trace))
     (POST "/updates" req (post req))
     (GET "/history/goto/:id" [id]
          (let [id (read-string id)
                ani (kernel/instantiate prob de.prob.statespace.AnimationSelector)
                t (.getCurrentTrace ani)
                t' (.gotoPosition t id)]
            (.traceChange ani t'))
          :ok)
     (resources "/")
     (not-found "Not Found")]))


(defrecord Routes [route-fn sente prob route-creator-fn]
  component/Lifecycle
  (start [this]
    (if route-fn
      this
      (do (println "Preparing Routes")
          (assoc this :route-fn (apply compojure.core/routes (route-creator-fn sente prob))))))
  (stop [this]
    (if route-fn
      (do (println "Destroying Routes")
          (dissoc this :route-fn))
      this)))

(defn mk-routes [route-creator-fn]
  (component/using (map->Routes {:route-creator-fn route-creator-fn}) [:sente :prob]))

(defrecord Handler [routes handler]
  component/Lifecycle
  (start [this]
    (if handler
      this
      (do (println "Creating Webapp")
          (assoc this
                 :handler
                 (let [handler
                       (wrap-defaults
                        (:route-fn routes)
                        site-defaults)]
                   (if (env :dev?)
                     (wrap-exceptions handler)
                     handler))))))
  (stop [this]
    (if handler
      (do (println "Destroying Webapp")
          (dissoc this :routes :handler))
      this)))

(defn mk-handler []
  (component/using (map->Handler {}) [:routes]))
