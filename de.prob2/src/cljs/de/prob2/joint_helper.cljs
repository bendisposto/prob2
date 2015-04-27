(ns de.prob2.joint-helper
  (:require [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.dagre-helper :as dh]))

(defn get-rect
  "takes a rendered dagre node object
  #js{:x -- :y -- :width -- :height -- :name --}"
  [node]
  (let [position {:x (.-x node) :y (.-y node)}
        size     {:width (.-width node) :height (.-height node)}
        attrs    {:rect {} :text {:text (.-name node)}}]
    (joint.shapes.basic.Rect. (clj->js {:position position
                                        :size     size
                                        :attrs    attrs}))))

(defn get-node-map
  "takes a rendered dagre-graph and calculates a map from node name to rendered joint Rect"
  [dagre-graph]
  (let [nodes  (dh/nodes dagre-graph)]
    (into {} (map (fn [e] [(.-name e) (get-rect e)]) nodes))))

(defn get-link
  "takes a rendered dagre edge object and a map of joint rects
  edge object: #js{:from -- :to -- :label -- :points [{:x -- :y --}{:x -- :y --}..{:x -- :y --}]}"
  [edge node-map]
  (let [source    {:id (.-id (node-map (.-from edge)))}
        target    {:id (.-id (node-map (.-to edge)))}
        labels    [{:position 0.5
                    :attrs {:text {:text (or (.-label edge) "")}}}]
      ;  vertices  (or (butlast (rest (.-points edge))) []) ; joint
                                        ; takes care of the start and
                                        ; end node positions
        router {:name "metro"}
        connector {:name "rounded" :args {:radius 10}}; {:name "normal"} ;
        attrs     {".marker-target" {:d "M 6 0 L 0 3 L 6 6 z"}}
       ]
    (joint.dia.Link. (clj->js {:source source :target target
                               :labels labels :router router
                                     ;   :vertices vertices
                               :connector connector
                               :smooth true   :attrs attrs}))))

(defn get-links
  "takes a rendered dagre-graph and a map of nodes and generates a list of edges"
  [dagre-graph node-map]
  (let [edges (dh/edges dagre-graph)]
    (mapv #(get-link % node-map) edges)))

(defn add-cells [graph cells]
  (do (.addCells graph (clj->js cells)) graph))

(defn graph-height [graph]
  (let [bbox (.getBBox graph (.getElements graph))]
    (if bbox (.-height bbox) 0)))

(defn graph-width [graph]
  (let [bbox (.getBBox graph (.getElements graph))]
    (if bbox (.-width bbox) 0)))

(defn mk-graph
  "creates a joint.dia.Graph"
  []
  (js/joint.dia.Graph.))

(defn mk-paper
  "takes a dom element and a graph and creates a joint.dia.Paper"
  [element graph]
  (js/joint.dia.Paper. #js{:el element :width 600 :height 200
                           :model graph :gridSize 1}))
