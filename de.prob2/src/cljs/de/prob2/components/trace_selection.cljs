(ns de.prob2.components.trace-selection
  (:require [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]))

(defn surrounding [v c n]
  (let [[a v'] (split-at (- c n) v)
        [b c] (split-at (inc (* n 2)) v')]
    [(count a) b (count c)]))

(defn p-fix [c]
  (when (< 0 c) [{:active "" :pp (str "..(" c ")..")}]))

(defn trace-excerpt [{:keys [transitions current-index] :as t}]
  (let [[pre hv post]
        (surrounding
         (into [] (map-indexed vector transitions))
         current-index 2)]
    ^{:key (h/fresh-id)}
    (concat
     (p-fix pre)
     (map (fn [[i e]]
            (let [a? (= i current-index)]
              (assoc e
                     :active (if a? "active" "")
                     :pp (if a?
                           (h/pp-transition e)
                           (h/fix-names (:name e)))))) hv)
     (p-fix post))))

(defn pp-trace-excerpt [t]
  ^{:key (h/fresh-id)}
  [:span {:class (str "pp-trace-item " (:active t))}
   (:pp t)])

(defn mk-span []
  ^{:key (h/fresh-id)} [:span ", "])

(defn mk-trace-item [{:keys [trace-id] :as p}]
  ^{:key (h/fresh-id)}
  [:li {:class "animator"}
   [:a {:href (str "#/trace/" trace-id)}
    (let [t (trace-excerpt p)]
      (if (seq t)
        (interpose [mk-span] (map pp-trace-excerpt t))
        "empty trace"))]])

(defn mk-animator-sublist [[id elems]]
  (let [trace-ids (map :trace-id elems)
        model (rf/subscribe [:model (first trace-ids)])
        {:keys [main-component-name filename]} @model]
    ^{:key (h/fresh-id)}
    [:li {:class "animator-sublist"}
     [:div {:class "model"}
      [:span {:class "glyphicon glyphicon-remove"
              :id "animator-remove-btn"
              :on-click
              #(rf/dispatch [:prob2/kill! {:trace-ids trace-ids}])}]
      [:span (str main-component-name " (" filename ")")]]
     [:ul {:class "animator-list"}
      (if (seq elems)
        (doall  (map mk-trace-item elems))
        [:div]
        )]]))

(defn trace-selection-view []
  (let [raw-traces (rf/subscribe [:traces])
        grouped (group-by :model (vals @raw-traces))]
    [:div {:class "trace-selector"}
     [:ul {:class "trace-list"}
      (if (seq grouped)
        (doall  (map mk-animator-sublist grouped))
        [:div])]]))


(rf/register-handler :prob2/kill! h/relay)

(rf/register-handler
 :de.prob2.kernel/trace-removed
 (comp rf/debug h/decode)
 (fn [sdb [_ traces]]
   (reduce (fn [db uuid] (h/dissoc-in db [:traces uuid])) sdb traces)))
