(ns de.prob2.sente
  (:require [taoensso.sente :as sente]
            [com.stuartsierra.component :as component]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream))

(declare send!)

(defn extract-action
  ([a] (extract-action nil a))
  ([_ {:keys [event]}] (keyword (name (first event)))))

(defn get-uid [a] (get-in a [:ring-req :session :uid]))

(defn reply [send-fn! req msg]
  (let [c (get-uid req)] (send-fn! c msg)))


(defmulti dispatch-ws extract-action)
(defmethod dispatch-ws :ws-ping [_ _])
(defmethod dispatch-ws :encoding  [{:keys [send-fn! encoding]} x]
  (reply send-fn! x  [:sente/encoding encoding]))



(defmethod dispatch-ws :default [_ a] (println (get-in a [:ring-req :session :uid]) (extract-action a)))


(defmulti handle-updates (fn [sente {:keys [event]}] (keyword (namespace (first event)))))
(defmethod handle-updates :chsk [sente a] (dispatch-ws sente a)) ;; do nothing
(defmethod handle-updates :default [sente a] (println :unknown-message a))

(defrecord Sente [post ws-handshake receive-channel send-fn! clients stop-routing-fn! encoding]
  component/Lifecycle
  (start [this]
    (if (:send-fn! this) this
        (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
                      connected-uids]}
              (sente/make-channel-socket! {})
              this' (assoc this

                           :post ajax-post-fn
                           :ws-handshake ajax-get-or-ws-handshake-fn
                           :receive-channel ch-recv
                           :send-fn! send-fn
                           :clients connected-uids)]
          (println "Initializing Websockets")
          (assoc this'  :stop-routing-fn! (sente/start-chsk-router! ch-recv (partial handle-updates this'))))))
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
