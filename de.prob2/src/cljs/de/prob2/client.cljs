(ns de.prob2.client
  (:require [de.prob2.helpers :as h]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [re-frame.core :as rf :refer [dispatch register-sub register-handler]]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(defn init-websocket []
  (let [{:keys [chsk ch-recv send-fn state]}
        (sente/make-channel-socket! "/updates" {:type :auto})]
    (add-watch state :connection-observer (fn [_ _ _ new] (dispatch [:connection-status (:open?  new)])))
    {:chsk chsk
     :ch-chsk ch-recv
     :send! send-fn
     :chsk-state state
     :stop! (sente/start-chsk-router!
             ch-recv
             (fn [e]
               (when (= (:id e) :chsk/recv)
                 (dispatch (vec (:?data e))))))}))


(defn reset-ui-state [ws]
   {:traces {} :models {} :states {} :results {} :websocket ws :encoding nil})

(defn default-ui-state []
  (reset-ui-state (init-websocket)))


(defn patch [sdb deltas]
  (h/deep-merge sdb deltas))


(register-handler
 :sente/encoding
 (comp  rf/debug h/decode h/with-send)
 (fn [db [_ enc send!]]
   (send! [:prob2/handshake {}])
   (assoc db :encoding enc)))

(register-handler
 :connection-status
 (comp  rf/debug)
 (fn [{ws :websocket :as db} [_ connected?]]
   (if connected? 
     (assoc db :connected? connected?)
     (reset-ui-state ws))))


(register-handler
 :de.prob2.kernel/ui-state
 (comp  h/decode)
 (fn [db [_ deltas]]
   (patch db deltas)))
