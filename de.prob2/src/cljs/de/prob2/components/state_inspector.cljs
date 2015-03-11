(ns de.prob2.components.state-inspector
  (:require [reagent.core :as reagent :refer [atom]]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf :refer
             [dispatch register-handler subscribe]]
            [de.prob2.helpers :refer [dissoc-in pp-transition fix-names fresh-id with-send decode]]))


(defn state-row [name current-value previous-value]
  [:tr [:td name] [:td current-value] [:td previous-value]])

(defn state-view []
  (let [id (session/get :focused-uuid)
        traces (subscribe [:traces])
        {:keys [trace-id current previous transition]} (get @traces id)
        names (map first (:values current))
        cvals (map second (:values current))
        pvals (map second (:values previous))]
    [:div (str  trace-id)]
    (into [:table {:class "table"}] (map (fn [n c p] [state-row n c p]) names cvals pvals))
    ))
