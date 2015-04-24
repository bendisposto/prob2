(ns de.prob2.dagre-helper
  (:require [taoensso.encore :as enc  :refer (logf log logp)]))

(defn add-node
  "node should be a map of form {:name -- :width -- :height--}"
  [g node]
 (do (.setNode g (:name node) (clj->js node)) g))

(defn add-edge
  "edge should be a map of form {:from -- :to -- :label --}"
  [g edge] 
  (do (.setEdge g (:from edge) (:to edge) (clj->js edge)) g))

(defn create-graph
  "creates either an empty dagre graph (no params) or with a collection of nodes and edges which will then be added with add-node and add-edge"
  ([nodes edges]
   (let [g0 (create-graph)
         g1 (reduce add-node g0 nodes)]
     (reduce add-edge g1 edges)))
  ([]
   (let [graph (js/global.dagre.graphlib.Graph.)]
     (.setGraph graph #js{})
     graph)))

(defn render
  "takes a dagre-graph and renders it"
  [g]
  (do (.layout js/global.dagre g) g))

(defn dagre-list->clj
  "the lists that are internally used by dagre aren't recognized by clojurescript. This function converts them to clojure lists"
  [list]
  (reverse (.reduce list (fn [l e] (cons e l)) [])))

(defn nodes
  "return a list of the node objects from the dagre graph"
  [g]
  (let [n (dagre-list->clj (.nodes g))]
    (mapv #(.node g %) n)))

(defn edges
  "return a list of the edge objects from the dagre graph"
  [g]
  (let [e (dagre-list->clj (.edges g))]
    (mapv #(.edge g %) e)))

(defn example-dagre-graph []
  (let [g (-> (create-graph)
              (add-node {:name "me" :width 30 :height 24})
              (add-node {:name "you" :width 30 :height 24})
              (add-edge {:from "me" :to "you" :label "us"})
              (render))]
    g))

