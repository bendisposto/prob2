(ns de.prob2.routing
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.dom.dataset]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [goog.dom.query]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
            [re-frame.core :as rf]
            [de.prob2.jsapi]
            [de.prob2.subs]
            [de.prob2.components.logo :refer [prob-logo]]
            [de.prob2.core :as core]
            [de.prob2.actions]
            [de.prob2.helpers :as h :refer [mk-url]]
            [de.prob2.components.modeline :refer [modeline]]
            [de.prob2.i18n :refer [i18n]])
  (:import goog.History))

;; -------------------------
;; Routes

(defn mk-routes []

  (secretary/set-config! :prefix "#")

  (secretary/defroute "/" []
    (session/put! :current-page #'core/home-page))

  
  (secretary/defroute "/trace/:uuid" [uuid]
    (session/put! :current-page #'core/animation-view)
    (session/put! :focused-uuid  (cljs.core/UUID. uuid)))

  (secretary/defroute "/hierarchy/:uuid" [uuid]
    (session/put! :current-page #'core/machine-hierarchy)
    (session/put! :focused-uuid  (cljs.core/UUID. uuid)))

  )

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


(defn preloader-waiting []
  [:div {:id "disconnected-screen"}
   [:h1 {:id "disconnected-msg"} (i18n :connecting)]
   [:img {:id "disconnected-img" :src "./img/disconnected.svg"}]])

(defn preloader-initializing []
  (.toggleClass (js/jQuery "#footer,#bg") "toggled")
  [:div])

(defn file-dialog []
  [:input {:style {:display "none"}
           :id "fileDialog"
           :type "file"
           :accept ".mch,.ref,.imp,.bum,.buc,.bcc,.bcm,.tla,.csp"
           :on-change (fn [e] (rf/dispatch [:open-file (-> e .-target .-value)]))}])

(rf/register-handler
 :open-file
 (fn [db [_ & file]]
   (if-not (seq file)
     (.click (.getElementById js/document "fileDialog"))
     (let [filename (first file)
           extension (last (re-find #".*\.(.*)" filename))]
       (logp :fn filename :ext extension)
       (rf/dispatch [:prob2/start-animation [filename extension]])))))

(rf/register-handler :prob2/start-animation h/relay)

(defn footer []
  (let [mc (rf/subscribe [:animator-count])]
    [:div
     [:span (i18n :hint-modeline)]
     [:span (str " -  R: " @mc " ")]
     [:span {:class "pull-right"} "(c) 2015"]]))


(defn current-page []
  [:div [(session/get :current-page)]])

(defn top-panel []
  (let [init?  (rf/subscribe [:initialised?])
        ready?  (rf/subscribe [:encoding-set?])
        connected? (rf/subscribe [:connected?])]
    (fn []
      [:div
       [file-dialog]
       (if-not @connected? (preloader-waiting)
               (do (when (and @init? (not @ready?)) (rf/dispatch [:chsk/encoding nil]))
                   (if-not @ready?
                     (preloader-initializing)
                     (do  (rf/dispatch [:populate-menus])
                          [:div
                           [current-page]]))))])))



(defn init-keybindings []
                                        ;  (.add js/shortcut "Ctrl+Space" #(rf/dispatch [:modeline :toggle]))
  )


(defn init! []
  #_(events/listen (.getElementById js/document "wrapper") "keydown" (fn [e] (log e)) true)
  (mk-routes)
  (hook-browser-navigation!)
  (init-keybindings)
  (rf/dispatch [:initialise-db])
  (r/render-component [modeline] (.getElementById js/document "minibuffer"))
  (r/render-component [top-panel] (.getElementById js/document "app"))
  (r/render-component [footer] (.getElementById js/document "footer-content"))
  )
