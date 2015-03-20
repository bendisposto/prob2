(ns de.prob2.core
  (:require-macros [hiccups.core :as hic])
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.schema :as schema]
            [de.prob2.client :as client]
            [re-frame.core :as rf]
            [hiccups.runtime :as hiccupsrt]
            [schema.core :as s]
            [de.prob2.helpers :as h]
            [reagent.session :as session]
            [de.prob2.components.trace-selection :refer [trace-selection-view]]
            [de.prob2.components.state-inspector :refer [state-view]]
            [de.prob2.components.history :refer [history-view]]
            [de.prob2.components.debug-component-structure :refer [mody]]
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


(defn title-string [kw]
  (let [s (name kw)
        [[f] l] (split-at 1 s)]
    (apply str (.toUpperCase (str f)) l)))

(defn render-line [[k c]]
  (let [e (map :label c)]
    [[:tr {:style "background-color: lightblue;"} [:td (title-string k)]]
     [:tr [:td (clojure.string/join " " e)]]]))

(defn render-box [n c]
  (let [kind (if (contains? c :events) "Machine" "Context")
        c' (into {} (remove (fn [[k v]] (empty? v)) c))
        elems (dissoc c' :invariants :name :variant :axioms)]
    (hic/html
     `[:div {:style "background-color: white;width:100px;top:-40px;right:-50px;position:relative;"}
       [:table {:style "width:100px;" :border 1}
        [:tr [:td ~(str kind " " n)]]
        ~@(mapcat render-line elems)
        ]])))


(rf/register-handler
 :hierarchy-update
 (fn [db [_ [dep-graph components] elem]]
   (let [nodes (map (fn [e] {:data {:id e :label (render-box e (get components e))}}) (keys  components))
         edges (map (fn [{:keys [from to type]}] {:data {:source from :target to :label (name type)}}) dep-graph)
         config {:container elem
                 :elements {:nodes nodes
                            :edges edges}
                 :renderer {:name "css"}
                 :layout {:name "breadthfirst"}
                 :style [{:selector "node"
                          :css {:shape "rectangle"
                                :width "100px"
                                :text-valign "center"
                                :text-halign "center"
                                :height "100px"
                                :background-color "white"
                                :content "data(label)"}}
                         {:selector "edge"
                          :css {:content "data(label)"
                                :target-arrow-shape "triangle"
                                :width "2px";
                                :text-outline-color "white"
                                :text-outline-opacity 1
                                :text-outline-width 3
                                :line-color "black"
                                }}]}]
     (js/cytoscape (clj->js config))
     (log (clj->js config))
     db)))

(defn hierarchy-view [id]
  (r/create-class
   { :component-did-mount
    (fn [c]
      (let [elem (.getDOMNode c)]
        (h/subs->handler :hierarchy-update [:hierarchy id] elem)))
    :display-name  "hierarchy-view"
    :reagent-render (fn [] [:div {:id "hierarchy-view"}])}))



(defn home-page []
  [trace-selection-view])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []
  (let [id (session/get :focused-uuid)]
    [:div {:id "h1"}
                                        ;  [mody id]

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
