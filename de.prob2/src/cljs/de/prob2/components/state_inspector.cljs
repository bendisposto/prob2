(ns de.prob2.components.state-inspector)


(defn state-row [name current-value previous-value]
  [:tr [:td name] [:td current-value] [:td previous-value]])

(defn state-view []
  (let [{:keys [trace-id current previous transition]} (:focused @state)
        names (map first (:values current))
        cvals (map second (:values current))
        pvals (map second (:values previous))]
    [:div (str  trace-id)]
    (into [:table {:class "table"}] (map (fn [n c p] [state-row n c p]) names cvals pvals))
    ))
