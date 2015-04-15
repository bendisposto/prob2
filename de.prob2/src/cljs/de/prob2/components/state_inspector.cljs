(ns de.prob2.components.state-inspector
  (:require [reagent.core :as r]
            [taoensso.encore :as enc :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]))


(defn state-row [current-value]
  [:tr
   #_[:td name]
   [:td current-value]
   #_[:td previous-value]])

(defn state-view [id]
  (let [x (rf/subscribe  [:current-state id])
        
                                        ;     names (map first (:values current))
                                        ;     cvals (map second (:values current))
                                        ;     pvals (map second (:values previous))
        ]
    [:div]
    (into [:table {:class "table"}]
          (map (fn [v] [state-row v]) (:current @x)))))
