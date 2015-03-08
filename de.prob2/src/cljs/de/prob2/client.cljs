(ns de.prob2.client
  (:require [cognitect.transit :as transit]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [taoensso.encore :as enc  :refer (logf log logp)]))

(defmulti handle first)

(defmethod handle :default [[t m]]
  (logp "Received Type: " t)
  (logp "Received Msg: " m))

(def encoding (clojure.core/atom nil))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/updates" ; Note the same path as before
                                  {:type :auto ; e/o #{:auto :ajax :ws}
                                   })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defn handshake [e]
  (clojure.core/reset! encoding e)
  (chsk-send! [:prob2/handshake {}]))


(defn read-transit [msg]
  (if @encoding (let [r (transit/reader @encoding)]
                  (transit/read r msg))
      (js/alert "No encoding transmitted from ProB")))


(sente/start-chsk-router!
 ch-chsk
 (fn [e]
   (when (= (:id e) :chsk/recv)
     (let [[e-type raw-msg] (:?data e)]
                                        ; (logp raw-msg)
       (if (= :sente/encoding e-type)
         (handshake (keyword raw-msg))
         (handle [e-type (read-transit raw-msg)]))))))

(defn send! [msg-type msg-map]
  (logp :sent :type msg-type :content msg-map)
  (chsk-send! [msg-type msg-map]))
