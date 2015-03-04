(ns de.prob2.sente
  (:require [taoensso.sente :as sente]
            [com.stuartsierra.component :as component]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream))


(defn extract-action
  ([a] (extract-action nil a))
  ([_ {:keys [event]}] (keyword (name (first event)))))


(defmulti dispatch-ws extract-action)
(defmethod dispatch-ws :ws-ping [x] ;(println :ping (get-in x
                                    ;[:ring-req :session :uid]))
  )
(defmethod dispatch-ws :default [a] (println :chsk a))


(defmulti handle-updates (fn [{:keys [event]}] (keyword (namespace (first event)))))
(defmethod handle-updates :chsk [a] (dispatch-ws a) ) ;; do nothing
(defmethod handle-updates :default [a] (println :unknown-message a))

(defrecord Sente [post ws-handshake receive-channel send-fn! clients stop-routing-fn! encoding]
  component/Lifecycle
  (start [this]
    (if (:send-fn! this) this
        (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
                      connected-uids]}
              (sente/make-channel-socket! {})
              this' (assoc this
                           :stop-routing-fn! (sente/start-chsk-router! ch-recv handle-updates)
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
