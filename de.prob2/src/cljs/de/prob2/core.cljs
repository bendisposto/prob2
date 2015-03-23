(ns de.prob2.core
  (:require-macros [de.prob2.macros :refer [remote-let]]
                   [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async]
            [reagent.core :as r]
            [de.prob2.generated.schema :as schema]
            [de.prob2.client :as client]
            [re-frame.core :as rf]
            [schema.core :as s]
            [de.prob2.helpers :as h]
            [reagent.session :as session]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [de.prob2.components.trace-selection :refer [trace-selection-view]]
            [de.prob2.components.state-inspector :refer [state-view]]
            [de.prob2.components.history :refer [history-view]]
            [de.prob2.components.hierarchy :refer [hierarchy-view]]
            [de.prob2.components.events :refer [events-view]]
            [de.prob2.components.dot-view :refer [dot-view]]))

;; -------------------------
;; Views


(defn validate-db
  [new-state]
  (try
    (s/validate schema/UI-State new-state)
    (catch js/Object e
      (.log js/console e)
      (logp new-state))))


(rf/register-handler
 :initialise-db
 rf/debug
 (fn [_ _] (client/default-ui-state)))

(rf/register-handler :chsk/encoding h/relay)

(defn home-page []
  [trace-selection-view])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []

  (let [id (session/get :focused-uuid)]
    [:div {:id "h1"}
     #_[dot-view "digraph simple { A->B }"]
     [history-view id]
     [events-view id]
     ]))


(defn machine-hierarchy []
  (let [id (session/get :focused-uuid)]
    [hierarchy-view id]))


(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
