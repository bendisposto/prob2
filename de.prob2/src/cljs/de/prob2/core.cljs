(ns de.prob2.core
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.schema :as schema]
            [de.prob2.client :as client]
            [re-frame.core :as rf]
            [schema.core :as s]
            [de.prob2.helpers :as h]
            [reagent.session :as session]
            [de.prob2.components.trace-selection :refer [trace-selection-view]]
            [de.prob2.components.state-inspector :refer [state-view]]
            [de.prob2.components.history :refer [history-view]]
            [de.prob2.components.events :refer [events-view]]))

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

#_(defn home-did-mount []
    (.addGraph js/nv (fn []
                       (let [chart (.. js/nv -models lineChart
                                       (margin #js {:left 100})
                                       (useInteractiveGuideline true)
                                       (transitionDuration 350)
                                       (showLegend true)
                                       (showYAxis true)
                                       (showXAxis true))]
                         (.. chart -xAxis
                             (axisLabel "x-axis")
                             (tickFormat (.format js/d3 ",r")))
                         (.. chart -yAxis
                             (axisLabel "y-axis")
                             (tickFormat (.format js/d3 ",r")))

                         (let [my-data [{:x 1 :y 5} {:x 2 :y 3} {:x 3 :y 4} {:x 4 :y 1} {:x 5 :y 2}]]

                           (.. js/d3 (select "#d3-node svg")
                               (datum (clj->js [{:values my-data
                                                 :key "my-red-line"
                                                 :color "red"
                                                 }]))
                               (call chart)))))))


(rf/register-handler
 :hierarchy-update
 (fn [db [_ dep-graph elem]]
   (logp dep-graph)
   (log elem)
   db))


(defn hierarchy-view []
  (r/create-class
   { :component-did-mount
    (fn [c]
      (let [id (session/get :focused-uuid)
            elem (.getDOMNode c)]
        (h/subs->handler :hierarchy-update [:hierarchy id] elem)))
    :display-name  "hierarchy-view"
    :reagent-render (fn [] [:div {:id "hierarchy-view"}])}))

(defn title-string [kw]
  (let [s (name kw)
        [[f] l] (split-at 1 s)]
   (apply str (.toUpperCase (str f)) l)))

(defn mk-entry [[k v]]
  [:li
   [:div (title-string k)
    [:ul
     (map (fn [e] [:li (:label e)])
          (remove (fn [e] (= "INITIALISATION" (:label e))) v))
     ]]])

(defn mody-component [[n c]]
  (let [kind (if (contains? c :events) "Machine" "Context")
        c' (into {} (remove (fn [[k v]] (empty? v)) c))
        elems (dissoc c' :invariants :name :variant :axioms)]
    (logp n elems)
    [:div
     [:div (str kind " " n)]
     [:ul
      (map mk-entry elems)]]))

(defn mody []
  (let [id (session/get :focused-uuid)
        model (rf/subscribe [:model id])
        formalism (:type @model)
        cs (:components @model)]
    [:div  (map mody-component cs)]))

#_(defn grx []
    (r/create-class
     {:component-did-mount home-did-mount
      :reagent-render
      (fn [] [:div
             [:h3 "Hallo d3"]
             [:div {:id "d3-node" :style {:height 150}} [:svg]]])}))

(defn home-page []
  [trace-selection-view])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []
  [:div {:id "h1"}
   [mody]
   [hierarchy-view]
   [history-view]
   [events-view]
   ])

(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
