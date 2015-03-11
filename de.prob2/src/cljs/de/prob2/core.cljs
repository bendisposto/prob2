(ns de.prob2.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [de.prob2.client :as client]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.schema :as schema]
            [re-frame.core :as rf :refer [dispatch register-sub register-handler]]
            [schema.core :as s]))

;; -------------------------
;; Views


(defn validate-db
  [new-state]
  (try
    (s/validate schema/UI-State new-state)
    (catch js/Object e
      (.log js/console e)
      (logp new-state))))


(defn default-ui-state []
  
  {:traces {} :models {} :states {} :results {} :websocket (client/init-websocket) :encoding nil})

(register-sub
 :initialised?
 (fn  [db]
   (reaction (not (empty? @db)))))

(register-sub
 :encoding-set?
 (fn  [db]
   (reaction (:encoding @db))))


(register-handler
 :initialise-db
 rf/debug
 (fn
   [_ _]                   ;; Ignore both params (db and v).
   (default-ui-state)))

(register-handler
 :fetch-encoding
 rf/debug
 (fn
   [{{send! :send!} :websocket :as db} _]
   (send! [:chsk/encoding nil])
   db))


(defn home-page []
                                        ;  [trace-selection-view]
  [:h1 "Du Mieser!!!"]
  )

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []
  [:div {:id "h1"} [history-view]])

(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
