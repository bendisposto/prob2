(ns de.prob2.components.hierarchy
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.dagre-helper :as dh]))

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

(defn get-joint-rect [node]
  (let [position {:x (.-x node) :y (.-y node)}
        size     {:width (.-width node) :height (.-height node)}
        attrs    {:rect {} :text {:text (.-name node)}}]
    (joint.shapes.basic.Rect. (clj->js {:position position
                                        :size     size
                                        :attrs    attrs}))))

(defn extract-nodes [dagre-graph]
  (let [nodes  (dh/nodes dagre-graph)]
    (into {} (map (fn [e] [(.-name e) (get-joint-rect e)]) nodes))))

(defn get-joint-link [edge node-map]
  (let [source    {:id (.-id (node-map (.-from edge)))}
        target    {:id (.-id (node-map (.-to edge)))}
        labels    [{:position 0.5 :attrs {:text {:text (or (.-label edge) "")}}}]
        vertices  (or (butlast (rest (.-points edge))) [])
        connector {:name "rounded" :args {:radius 50}}
        attrs     {".marker-target" {:d "M 6 0 L 0 3 L 6 6 z"}}
       ]
    (joint.dia.Link. (clj->js {:source source :target target
                               :labels labels :vertices vertices
                               :connector connector
                               :smooth true   :attrs attrs}))))

(defn extract-links [dagre-graph node-map]
  (let [edges (dh/edges dagre-graph)]
    (mapv #(get-joint-link % node-map) edges)))

(defn create-canvas [model]
;  (logp @model)
  (fn [] [:div {:id (str "hierarchy-view" (:main-component-name @model)) :style {:height 0}}
          [:div]]))

(defn draw-joint-graph [element dep-graph component-names]
  (let [graph (js/joint.dia.Graph.)
        m     #js {:el element :width 600 :height 200
                   :model graph :gridSize 1}
        paper (js/joint.dia.Paper. m)
        dagre-graph     (calculate-dimensions component-names dep-graph)
        nodes (extract-nodes dagre-graph)
        links (extract-links dagre-graph nodes)
        _     (.addCells graph (clj->js (vals nodes)))
        _     (.addCells graph (clj->js links))
        dimensions (.getBBox graph (.getElements graph))
        ]
    (set! (.-height (.-style element)) (+ 100 (.-height dimensions)))))

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
