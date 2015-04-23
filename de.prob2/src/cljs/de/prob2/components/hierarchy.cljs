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
  (logp nodes dep-graph)
  (let [vertices (mapv extract-vertice nodes)
        edges    (mapv extract-edge dep-graph)
        graph    (create-graph vertices edges)]
    (render graph)))

(defn weird-dagre-list-to-clj [list]
  (reverse (.reduce list (fn [l e] (cons e l)) [])))

(defn get-joint-rect [node]
  (let [position {:x (.-x node) :y (.-y node)}
        size     {:width (.-width node) :height (.-height node)}
        attrs    {:rect {} :text {:text (.-name node)}}]
    (joint.shapes.basic.Rect. (clj->js {:position position
                                        :size     size
                                        :attrs    attrs}))))

(defn extract-nodes [dagre-graph]
  (let [n  (weird-dagre-list-to-clj (.nodes dagre-graph))
        nodes (mapv  #( .node dagre-graph %) n)]
    (logp nodes)
    (into {} (map (fn [e] [(.-name e) (get-joint-rect e)]) nodes))))

(defn get-joint-link [edge node-map]
  (logp :xxm edge node-map)
  (let [source    {:id (.-id (node-map (.-from edge)))}
        target    {:id (.-id (node-map (.-to edge)))}
        labels    [{:position 0.5 :attrs {:text {:text (or (.-label edge) "")}}}]
        vertices  (or (butlast (rest (.-points edge))) [])
       ]
    (joint.dia.Link. (clj->js {:source source :target target
                                :labels labels :vertices vertices}))))

(defn extract-links [dagre-graph node-map]
  (let [e (weird-dagre-list-to-clj (.edges dagre-graph))
        edges (map #(.edge dagre-graph %) e)]
    (.log js/console edges)
    (map #(get-joint-link % node-map) edges)))

(defn create-canvas [model]
;  (logp @model)
  (fn [] [:div {:id (str "hierarchy-view" (:main-component-name @model)) :style {:height 0}}
          [:div]]))

(defn create-component [model]
  (fn [x] (let [dep-graph (:dependency-graph @model)
                component-names (keys (:components @model))
                graph (js/joint.dia.Graph.)
                element (.getDOMNode x)
                m     #js {:el element
                           :width 600 :height 200
                           :model graph :gridSize 1}
                paper (js/joint.dia.Paper. m)
                g     (calculate-dimensions component-names dep-graph)
                nodes (extract-nodes g) 
                links (extract-links g (clj->js nodes)) 
               ; _     (logp links) 
                                        ; _     (.log js/console (first (vals nodes)))
                _     (logp nodes)
                _     (.addCells graph (clj->js (vals nodes)))
                dimensions (.getBBox graph (.getElements graph))
                _     (.log js/console dimensions)
                                        ;    _     (.log js/console (js/$ (.getDOMNode x)))
                ] 
            (set! (.-height (.-style element)) (+ 100 (.-height dimensions))))))

(defn hierarchy-view [id]
  (let [model (rf/subscribe [:model id])]
    (r/create-class
     {:component-did-update (create-component model)
      :component-did-mount  (create-component model) ; for figwheel debugging
      :reagent-render (create-canvas model)})))
