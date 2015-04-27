(ns de.prob2.components.hierarchy
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.dagre-helper :as dh]
            [de.prob2.joint-helper :as jh]))

(defn extract-vertice [e]
  (let [width (if (< 10 (count e)) (* 10 (count e)) 100)]
    {:name e :label e :width width :height 30}))

(defn extract-edge [edge]
  (let [t (:type edge)] (assoc edge :label (name t))))

(defn calculate-dimensions [rel-graph]
  (let [ vertices (mapv (fn [v] (extract-vertice (first v))) rel-graph)
         edges    (mapv extract-edge (mapcat second rel-graph))
        graph    (dh/create-graph vertices edges)]
    (dh/render graph)))

(defn create-canvas [model]
  (fn [] [:div {:id (str "hierarchy-view" (:main-component-name @model)) :style {:height 0}}
          [:div]]))

(defn priority-type [{:keys [type]}]
  (if (= type :refines) 0 1))

(defn model-order [element dep-graph]
  (let [next (get dep-graph element [])
        sorted-next (sort-by priority-type next)
        extracted-next (mapv (fn [{:keys [to]}]
                               (model-order to dep-graph)) sorted-next)]
    (cons [element sorted-next] (distinct (apply concat extracted-next)))))

(defn draw-joint-graph [element main-comp dep-graph]
  (let [rel-graph (model-order main-comp (group-by :from dep-graph))
        graph (jh/mk-graph)
        paper (jh/mk-paper element graph)
        dagre-graph     (calculate-dimensions rel-graph)
        nodes (jh/get-node-map dagre-graph)
        links (jh/get-links dagre-graph nodes)
        graph2 (-> graph (jh/add-cells (vals nodes))
                   (jh/add-cells links))]
    (set! (.-height (.-style element)) (+ 100 (jh/graph-height graph2)))
    (set! (.-width  (.-style element)) (+ 100 (jh/graph-width graph2)))))

(defn create-component [model]
  (fn [x] (let [dep-graph (:dependency-graph @model)
                component-names (keys (:components @model))
                main-comp (:main-component-name @model)
                element (.getDOMNode x)]
            (when (and dep-graph main-comp element)
              (draw-joint-graph element main-comp dep-graph)))))

(defn hierarchy-view [id]
  (let [model (rf/subscribe [:model id])]
    (r/create-class
     {:component-did-update (create-component model)
      :component-did-mount  (create-component model) ; for figwheel debugging
      :reagent-render (create-canvas model)})))
