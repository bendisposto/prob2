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
     ;   _ (.log js/console (into [] (map (fn [x] {:element x}) k)))
     ;   vertices (map (fn [e] {:name e :label e :width 100 :height 100}) k)
        edges  (map (fn [edge] (let [t (:type edge)]
                          (-> edge (dissoc :type)
                              (assoc :label (name t))))) dep-graph)
        ]
    
    [:div (dh/example)]))
