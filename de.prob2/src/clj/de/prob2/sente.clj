(ns de.prob2.sente
  (:require [taoensso.sente :as sente]
            [com.stuartsierra.component :as component]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream)
  )


(defn extract-action
  ([a b] (extract-action nil a b))
  ([_ {:keys [event]} _] (keyword (name (first event)))))


(defmulti dispatch-ws extract-action)
(defmethod dispatch-ws :ws-ping [_ _])
(defmethod dispatch-ws :default [a b] (println :chsk a b))


(defmulti handle-updates (fn [{:keys [event]} _] (keyword (namespace (first event)))))
(defmethod handle-updates :chsk [a b] (dispatch-ws a b) ) ;; do nothing
(defmethod handle-updates :default [a b] (println :unknown-message a b))

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
