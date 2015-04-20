(ns de.prob2.components.dot-view
  (:require [de.prob2.dagre-helper :as d]
             [taoensso.encore :as enc  :refer (logf log logp)]))


(defn dot-view [dot-string]
  (let [parsed (.read js/graphlibDot dot-string)
        layouted (d/render parsed)
        ]
    (d/to-svg layouted)))
