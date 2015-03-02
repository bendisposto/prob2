(ns de.prob2.core
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.dom.query]
            [goog.array]
            [goog.dom.dataset]
            [cognitect.transit :as transit]
            [goog.history.EventType :as EventType]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [clojure.data]
            [taoensso.encore :as enc    :refer (logf log logp)]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

;; -------------------------
;; Views
                                        ;(sente/set-logging-level! :trace)


(def trace (atom {}))


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


(defn fix-names [{:keys [history] :as t}]
  (assoc
   t
   :history
   (map
    (fn [e]
      (assoc e
             :name (get {"$initialise_machine" "INITIALIZATION"
                         "$setup_constants" "SETUP CONSTANTS"} (:name e) (:name e)))) history)))


(defmulti handle first)

(defmethod handle :de.prob2.kernel/model-changed [[_ m]]
  (logp "Model changed"))

(defmethod handle :de.prob2.kernel/trace-changed [[_ {:keys [trace-id current previous history current-index] :as t}]]
  (reset! trace (fix-names t))
  (logp "Trace: " (str trace-id))
  (logp "Current State: " current)
  (logp "Previous State: " previous)
  (logp "History: " history)
  (logp "History index: " current-index)
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

(defn null-component [] [:div "Not yet implemented"])

(defn state-row [name current-value previous-value]
  [:tr [:td name] [:td current-value] [:td previous-value]])

(defn state-view []
  (let [{:keys [trace-id current previous transition]} @trace
        names (map first (:values current))
        cvals (map second (:values current))
        pvals (map second (:values previous))]
    [:div (str  trace-id)]
    (into [:table {:class "table"}] (map (fn [n c p] [state-row n c p]) names cvals pvals))
    ))


(defn pp-transition [{:keys [name parameters return-values]}]
  (let [ppp (if (seq parameters) (str "(" (clojure.string/join "," parameters) ")") "")
        pprv (if (seq return-values) (str (clojure.string/join "," return-values) \u21DC " ")  "")] (str pprv name ppp)))

(defn- mk-history-item [current index item]
  ^{:key (str "h" index)}
  [:li {:class (str "history-item" (cond (= current index) " current " (< current index) " future "  :default ""))
        :on-click (fn [_] (GET (str "/history/goto/" index)))}
   (pp-transition item)])

(defn history-view []
  (let [sort-order (atom identity)]
    (fn []
      (let [t @trace]
        [:div {:class "history-view"}
         [:span {:class "glyphicon glyphicon-sort pull-right"
                 :id "sort-button"
                 :on-click (fn [_] (swap! sort-order
                                         (fn [f] (get {identity reverse} f identity))))}]
         [:ul {:class "history-list"}
          (map-indexed (partial mk-history-item (:current-index t)) (@sort-order (:history t)))]
         ]))))


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

(secretary/defroute "/stateview" []
  (session/put! :current-page #'state-view))

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

(def components {"state-view" state-view
                 "history-view" history-view})


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

(defn init! []
  (hook-browser-navigation!)
  (setup-components))
