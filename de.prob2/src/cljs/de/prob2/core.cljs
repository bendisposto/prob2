(ns de.prob2.core
  (:require-macros [hiccups.core :as hic])
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


(rf/register-handler
 :hierarchy-update
 (fn [db [_ dep-graph elem]]
   (let [nodes (into #{} (concat (map :from dep-graph) (map :to dep-graph)))
         vnodes (map (fn [e] {:data {:id e :label (str "<h1>" e "</h1>")}}) nodes)
         vedges (map (fn [{:keys [from to type]}] {:data {:source from :target to :label (name type)}}) dep-graph)
         config {:container elem
                 :elements {:nodes vnodes
                            :edges vedges}
                 :renderer {:name "css"}
                 :style [{:selector "node"
                          :css {:shape "rectangle"
                                :content "data(label)"}}
                         {:selector "edge"
                          :css {:content "data(label)"
                                :target-arrow-shape "triangle"
                                :line-color  "black"
                                :target-arrow-color "black"
                                :width "2px";
                                }}]}]
     (js/cytoscape (clj->js config))
     (log (clj->js config))
     (logp dep-graph)
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
     [hierarchy-view id]
     [history-view id]
     [events-view id]
     ]))

(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
