(ns de.prob2.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [de.prob2.helpers :as h]
            [cljs.core.async :as async]
            [cljs.core.async.impl.channels :refer [ManyToManyChannel]]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [re-frame.core :as rf :refer [dispatch register-sub register-handler]]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(def callback-id (atom 0))
(defn fresh-id [] (swap! callback-id inc))


(defn init-websocket []
  (let [{:keys [chsk ch-recv send-fn state]}
        (sente/make-channel-socket! (str h/host ":" h/port "/updates") {:type :auto})]
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


(defn- reset-ui-state [ws]
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

(register-handler
 :sente/reply
 (fn [db [_ & x]]
   (logp x)
   db))

(rf/register-handler
 :prob2/call
 (fn [{{send! :send!} :websocket :as db}
     [t continuation type result-transform command & args]]
   (let [caller-id (fresh-id)
         db' (assoc-in db
                       [:callbacks caller-id]
                       {:result-transform result-transform
                        :code continuation})]
     (send! [t {:command command
                :type type
                :args args
                :caller-id caller-id}])
     db')))


(rf/register-handler
 :de.prob2.kernel/response
 h/decode
 (fn [db [_ {:keys [caller-id result] :as resp}]]
   (logp resp)
   (let [callback (get-in db [:callbacks caller-id :code])
         transform (get-in db [:callbacks caller-id :result-transform])
         db' (h/dissoc-in db [:callbacks caller-id])
         res (transform result)]
     (cond
       (fn? callback) (callback res)
       (instance? ManyToManyChannel callback) (go (async/>! callback res) (async/close! callback))
       :otherwise (logp "Unknown callback type " (type callback) res))
     db')))
