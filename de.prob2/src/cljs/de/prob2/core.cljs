(ns de.prob2.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [de.prob2.client :as client]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.schema :as schema]
            [schema.core :as s]))

;; -------------------------
;; Views
                                        ;(sente/set-logging-level! :trace)

(declare home-page disconnected-page)

(def *validation* false)

(def state (atom {:traces {} :models {} :states {} :results {} :connected false}
                 :validator
                 (fn [new-state]
                   (logp :new-state new-state)
                   (if  *validation*
                     (try
                       (s/validate schema/UI-State new-state)
                       (catch js/Object e
                         (.log js/console e)
                         (logp new-state)))
                     new-state))))


(def id-store (clojure.core/atom 0))
(defn fresh-id []
  (let [x @id-store]
    (swap! id-store inc) x))

(defn connect []
  (client/chsk-send! [:chsk/encoding nil])
  (swap! state assoc :connected true)
  (session/put! :current-page #'home-page))

(defn disconnect []
  (session/put! :current-page #'disconnected-page)
  (reset! state {:traces {} :connected false}))

(add-watch
 client/chsk-state
 :chsk-observer
 (fn [_ _ {oo :open?} {no :open?}]
   (cond (and no (not oo)) (connect)
         (and oo (not no)) (disconnect)
         :otherwise nil)))


(defn fix-names [name]
  (get {"$initialise_machine" "INITIALISATION"
        "$setup_constants" "SETUP CONSTANTS"} name name))

(defn dissoc-in
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (assoc m k newmap))
      m)
    (dissoc m k)))


(defmethod client/handle :de.prob2.kernel/ui-state [[_ msgs]]
  (doseq [[uuid trace] (:traces msgs)]
    (swap! state assoc-in [:traces uuid] trace )))

(defmethod client/handle :de.prob2.kernel/trace-removed [[_ msgs]]
  (doseq [uuid msgs]
    (swap! state dissoc-in [:traces uuid])))




(defn pp-transition [{:keys [name parameters return-values]}]
  (let [ppp (if (seq parameters) (str "(" (clojure.string/join "," parameters) ")") "")
        pprv (if (seq return-values) (str (clojure.string/join "," return-values) \u21DC " ")  "")
        fname (fix-names name)] (str pprv fname ppp)))



(defn home-page []
  [trace-selection-view])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []
  [:div {:id "h1"} [history-view]])

(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
