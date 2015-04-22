(ns de.prob2.components.hierarchy
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.dagre-helper :as dh]))

(defn add-node [g node]
  (do (.setNode g (:name node) (clj->js node)) g))

(defn add-edge
  ([g edge]
   (do (.setEdge g (:from edge) (:to edge) (clj->js edge)) g)))

(defn create-graph [nodes edges]
  (let [graph (js/global.dagre.graphlib.Graph.)
        _     (.setGraph graph #js{})
        g1    (reduce add-node graph nodes)]
    (reduce add-edge g1 edges)))
(defn render [g] (do (.layout js/global.dagre g) g))

(defn extract-vertice [e]
  (let [width (if (< 10 (count e)) (* 10 (count e)) 100)]
    {:name e :width width :height 30}))

(defn extract-edge [edge]
  (let [t (:type edge)] (assoc edge :label (name t))))

(defn calculate-dimensions [nodes dep-graph]
  (let [vertices (map extract-vertice nodes)
        edges    (map extract-edge dep-graph)
        graph    (create-graph vertices edges)]
    (render graph)))

(defn extract-nodes [dagre-graph]
  (for [node-name (concat #js[] (.nodes dagre-graph))]
    (.node dagre-graph node-name)))

(defn create-canvas []
  [:div {:id "hierarchy-view"}])

(defn create-component [component-names dep-graph]
  (fn [x] (let [graph (js/joint.dia.Graph.)
                m     #js {:el (.getDOMNode x)
                           :width 600 :height 200
                           :model graph :gridSize 1}
                paper (js/joint.dia.Paper. m)
                g     (calculate-dimensions component-names dep-graph)
                _     (.log js/console (extract-nodes g))])))

(defn hierarchy-view [id]
  (let [model (rf/subscribe [:model id])
        dep-graph (:dependency-graph @model)
        component-names   (keys (:components @model))]
    (r/create-class
     {:component-did-mount (create-component component-names dep-graph)
      :reagent-render create-canvas})))
