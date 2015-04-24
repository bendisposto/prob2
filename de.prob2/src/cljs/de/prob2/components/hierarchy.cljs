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
    {:name e :width width :height 30}))

(defn extract-edge [edge]
  (let [t (:type edge)] (assoc edge :label (name t))))

(defn calculate-dimensions [nodes dep-graph]
  (let [vertices (mapv extract-vertice nodes)
        edges    (mapv extract-edge dep-graph)
        graph    (dh/create-graph vertices edges)]
    (dh/render graph)))

(defn create-canvas [model]
  (fn [] [:div {:id (str "hierarchy-view" (:main-component-name @model)) :style {:height 0}}
          [:div]]))

(defn draw-joint-graph [element dep-graph component-names]
  (let [graph (jh/mk-graph)
        paper (jh/mk-paper element graph)
        dagre-graph     (calculate-dimensions component-names dep-graph)
        nodes (jh/get-node-map dagre-graph)
        links (jh/get-links dagre-graph nodes)
        graph2 (-> graph (jh/add-cells (vals nodes))
                   (jh/add-cells links))
        ]
    (set! (.-height (.-style element)) (+ 100 (jh/graph-height graph2)))))

(defn create-component [model]
  (fn [x] (let [dep-graph (:dependency-graph @model)
                component-names (keys (:components @model))
                element (.getDOMNode x)]
            (when (and dep-graph component-names element)
              (draw-joint-graph element dep-graph component-names)))))

(defn hierarchy-view [id]
  (let [model (rf/subscribe [:model id])]
    (r/create-class
     {:component-did-update (create-component model)
      :component-did-mount  (create-component model) ; for figwheel debugging
      :reagent-render (create-canvas model)})))
