(ns de.prob2.components.hierarchy
  (:require-macros [hiccups.core :as hic])
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]))



(defn render-line [[k c]]
  (let [e (map :label c)]
    [[:tr {:style "background-color: lightblue;"} [:td (h/title-case k)]]
     [:tr [:td (clojure.string/join " " e)]]]))

(defn render-box [n c]
  (let [kind (if (contains? c :events) "Machine" "Context")
        c' (into {} (remove (fn [[k v]] (empty? v)) c))
        elems (dissoc c' :invariants :name :variant :axioms)]
    (hic/html
     `[:div {:style "background-color: white;width:100px;top:-40px;right:-50px;position:relative;"}
       [:table {:style "width:100px;" :border 1}
        [:tr {:style "background-color: black; color: white"} [:td ~(str kind " " n)]]
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
                                :target-arrow-color "black"
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
