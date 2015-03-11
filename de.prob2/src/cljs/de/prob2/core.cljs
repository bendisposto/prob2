(ns de.prob2.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.schema :as schema]
            [de.prob2.client :as client]
            [re-frame.core :as rf :refer [dispatch register-sub register-handler]]
            [schema.core :as s]
            [de.prob2.components.trace-selection :refer [trace-selection-view]]
            [de.prob2.components.state-inspector :refer [state-view]]
            [de.prob2.components.history :refer [history-view]]))

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


(register-handler
 :initialise-db
 rf/debug
 (fn [_ _] (default-ui-state)))

(register-handler
 :fetch-encoding
 rf/debug
 (fn
   [{{send! :send!} :websocket :as db} _]
   (send! [:chsk/encoding nil])
   db))


(defn home-page []
  [trace-selection-view])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []
  [:div {:id "h1"} [history-view]
   [state-view]])

(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
