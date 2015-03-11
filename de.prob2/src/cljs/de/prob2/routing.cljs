(ns de.prob2.routing
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.dom.dataset]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [goog.dom.query]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
            [re-frame.core :as rf :refer [dispatch register-sub register-handler]]
            [de.prob2.subs]
            [de.prob2.core :as core])
  (:import goog.History))

;; -------------------------
;; Routes

(defn mk-routes []

  (secretary/set-config! :prefix "#")  

  (secretary/defroute "/" []
    (session/put! :current-page #'core/home-page))

  (secretary/defroute "/about" []
    (session/put! :current-page #'core/about-page))

  (secretary/defroute "/trace/:uuid" [uuid]
    (session/put! :current-page #'core/animation-view)
    (session/put! :focused-uuid  (cljs.core/UUID. uuid)))

  #_(secretary/defroute "/stateview" []
      (session/put! :current-page #'core/state-view)))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Components


(defn current-page []
  [:div [(session/get :current-page)]])

(defn top-panel    ;; this is new
  []
  (let [init?  (rf/subscribe [:initialised?])
        ready?  (rf/subscribe [:encoding-set?])]
    (fn []
      (when (and @init? (not @ready?)) (dispatch [:fetch-encoding]))
      (if-not @ready?
           [:h1 "Initialising ..."]
            [current-page]))))

(defn init! []
  (mk-routes)
  (hook-browser-navigation!)
  (dispatch [:initialise-db])
  (reagent/render-component [top-panel] (.getElementById js/document "app")))
