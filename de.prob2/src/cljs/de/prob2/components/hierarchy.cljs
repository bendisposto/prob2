(ns de.prob2.components.hierarchy
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.dagre-helper :as dh]))


(defn hierarchy-view [id]
  (let [model (rf/subscribe [:model id])
        dep-graph (:dependency-graph @model)
        k       (keys (:components @model))
        vertices (map (fn [e] {:name e :label e :width 50 :height 30}) k)
        edges  (map (fn [edge] (let [t (:type edge)] (assoc edge :label (name t)))) dep-graph)
        _  (logp :v vertices)
        g (dh/create-graph {:nodes vertices :edges edges})
       ; g2 (reduce dh/add-node g vertices)
        ]
    [:div (dh/to-svg (dh/render g))]))
