(ns de.prob2.client
  (:require [de.prob2.helpers :refer [decode with-send]]
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





(defn patch [sdb {traces :traces}]
  (reduce
   (fn [db [uuid content]] (assoc-in db [:traces uuid] content))
   sdb
   traces))


(register-handler
 :sente/encoding
 (comp  rf/debug decode with-send)
 (fn [db [_ enc send!]]
   (send! [:prob2/handshake {}])
   (assoc db :encoding enc)))

(register-handler
 :connection-status
 (comp  rf/debug)
 (fn [db [_ connected?]]
   (assoc db :connected? connected?)))


(register-handler
 :de.prob2.kernel/ui-state
 (comp  decode)
 (fn [db [_ deltas]]
   (patch db deltas)))
