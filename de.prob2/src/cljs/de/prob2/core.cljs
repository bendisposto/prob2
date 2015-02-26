(ns de.prob2.core
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [cognitect.transit :as transit]
            [goog.history.EventType :as EventType]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [clojure.data]
            [taoensso.encore :as enc    :refer (logf log logp)]
            [cljsjs.react :as react])
  (:import goog.History))

;; -------------------------
;; Views
                                        ;(sente/set-logging-level! :trace)


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/updates" ; Note the same path as before
                                  {:type :auto ; e/o #{:auto :ajax :ws}
                                   })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defn read-transit [msg]
  (let [r (transit/reader :json-verbose)]
    (transit/read r msg)))


(defmulti handle first)

(defmethod handle :de.prob2.kernel/model-changed [[_ m]]
  (logp "Model changed"))

(defmethod handle :de.prob2.kernel/trace-changed [[_ {:keys [trace-id current previous transition]}]]
  (logp "Trace: " trace-id)
  (logp "Current State: " current)
  (logp "Previous State: " previous)
  (logp "Transition: " transition)
  (logp "Diff: " (keys (first (clojure.data/diff (:values current) (:values  previous))))))


(defmethod handle :default [[t m]]
  (logp "Received Type: " t)
  (logp "Received Msg: " m))


(sente/start-chsk-router!
 ch-chsk
 (fn [e]
   (when (= (:id e) :chsk/recv)
     (let [[e-type raw-msg] (:?data e)]
       (handle [e-type (read-transit raw-msg)])))))

(defn home-page []
  [:div [:h2 "Welcome to ProB 2.0"]
   [:div [:a {:href "#/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

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
;; Initialize app
(defn init! []
  (hook-browser-navigation!)
  (reagent/render-component [current-page] (.getElementById js/document "app")))
