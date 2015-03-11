(ns de.prob2.routing
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.dom.dataset]
            [goog.dom.query]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
            [de.prob2.core :as core]
            [de.prob2.dataflow]
            [de.prob2.event-handler])
  (:import goog.History))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'core/home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'core/about-page))

(secretary/defroute "/trace/:uuid" [uuid]
  (session/put! :current-page #'core/animation-view)
  (session/put! :focused-uuid  (cljs.core/UUID. uuid)))

(secretary/defroute "/stateview" []
  (session/put! :current-page #'core/state-view))

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

(def components {"state-view" core/state-view
                 "history-view" core/history-view
                 "trace-selection-view" core/trace-selection-view})

(defn null-component [] [:div "Not yet implemented"])

;; -------------------------
;; Initialize app

(defn ^:export register
  ([component-name gui-id settings]
   (println settings)
   (when-let [t (. js/document (getElementById gui-id))]
     (reagent/render-component [(get components component-name null-component)] t))))

(defn setup-components []
  (let [cs (goog.dom.query "div[data-type]")]
    (doseq [c (array-seq cs 0)]
      (register (goog.dom.dataset/get c "type") (.-id c) (js->clj (goog.dom.dataset/getAll c))))))

(defn current-page []
  [:div [(session/get :current-page)]])

(defn init! []
  (hook-browser-navigation!)
  (setup-components)
  (reagent/render-component [current-page] (.getElementById js/document "app")))
