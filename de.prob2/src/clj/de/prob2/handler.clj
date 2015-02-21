(ns de.prob2.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [selmer.parser :refer [render-file]]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async :refer (<! <!! >! >!! put! chan go go-loop)]
            [prone.middleware :refer [wrap-exceptions]]
            [environ.core :refer [env]]
            [taoensso.sente :as sente]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream))


(defmulti handle-updates (fn [{:keys [event]} _] (first event)))
(defmethod handle-updates :chsk/ws-ping [_ _]) ;; do nothing
(defmethod handle-updates :de.prob2/hello [_ _] (println :hello))
(defmethod handle-updates :default [e c] (println e))

(defrecord Sente [post ws-handshake receive-channel send-fn! clients stop-routing-fn! encoding]
  component/Lifecycle
  (start [this]
    (if (:send-fn! this) this
        (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
                      connected-uids]}
              (sente/make-channel-socket! {})
              this' (assoc this
                      :stop-routing-fn! (sente/start-chsk-router-loop! handle-updates ch-recv)
                      :post ajax-post-fn
                      :ws-handshake ajax-get-or-ws-handshake-fn
                      :receive-channel ch-recv
                      :send-fn! send-fn
                      :clients connected-uids)]
          (println "Initializing Websockets")
          this')))
  (stop [this]
    (if send-fn!
      (do (println "Stopping Message Handling")
          (stop-routing-fn!)
          (println "Destroying Websockets")
          (assoc this
            :stop-routing-fn! nil
            :post nil
            :ws-handshake nil
            :receive-channel nil
            :send-fn! nil
            :clients nil))
      this)))

(defn- encode [message encoding]
  (let [s (ByteArrayOutputStream. 4096)
        w (transit/writer s encoding)]
    (transit/write w message)
    (.toString s)))

(defn send! [{:keys [send-fn! encoding]} user-id event-id message]
  (send-fn! user-id [event-id (encode message encoding)]))

(defn mk-sente [] (map->Sente {:encoding :json-verbose}))

(defn default-routes []
  (fn [{:keys [ws-handshake post]}]
    [(GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))
     (GET  "/updates" req (ws-handshake req))
     (POST "/updates" req (post req))
     (resources "/")
     (not-found "Not Found")]))


(defrecord Routes [route-fn sente prob route-creator-fn]
  component/Lifecycle
  (start [this]
    (if route-fn
      this
      (do (println "Preparing Routes")
          (assoc this :route-fn (apply compojure.core/routes (route-creator-fn sente))))))
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
          (dissoc this :routes :hadler))
      this)))

(defn mk-handler []
  (component/using (map->Handler {}) [:routes]))
