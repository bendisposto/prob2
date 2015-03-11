(ns de.prob2.components.trace-selection
  (:require [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf :refer
             [dispatch register-handler subscribe]]
            [de.prob2.helpers :refer [dissoc-in pp-transition fix-names fresh-id with-send decode]]))

(defn surrounding [v c n]
  (let [[a v'] (split-at (- c n) v)
        [b c] (split-at (inc (* n 2)) v')]
    [(count a) b (count c)]))

(defn p-fix [c]
  (when (< 0 c) [{:active "" :pp (str "..(" c ")..")}]))

(defn trace-excerpt [{:keys [history current-index] :as t}]
  (let [[pre hv post]
        (surrounding
         (into [] (map-indexed vector history))
         current-index 2)]
    ^{:key (fresh-id)}
    (concat
     (p-fix pre)
     (map (fn [[i e]]
            (let [a? (= i current-index)]
              (assoc e
                     :active (if a? "active" "")
                     :pp (if a?
                           (pp-transition e)
                           (fix-names (:name e)))))) hv)
     (p-fix post))))

(defn pp-trace-excerpt [t]
  ^{:key (fresh-id)}
  [:span {:class (str "pp-trace-item " (:active t))}
   (:pp t)])

(defn mk-span []
  ^{:key (fresh-id)} [:span ", "])

(defn mk-trace-item [{:keys [current-index trace-id history] :as p}]
  ^{:key trace-id}
  [:li {:class "animator"}
   [:a {:href (str "#/trace/" trace-id)}
    (let [t (trace-excerpt p)]
      (if (seq t)
        (interpose [mk-span] (map pp-trace-excerpt t))
        "empty trace"))]])

(defn mk-animator-sublist [[id elems]]
  (let [trace-ids (map :trace-id elems)
        {:keys [animator-id main-component-name filename]} (:model (first elems))]
    ^{:key (fresh-id)}
    [:li {:class "animator-sublist"}
     [:div {:class "model"}
      [:span {:class "glyphicon glyphicon-remove"
              :id "animator-remove-btn"
              :on-click
              #(dispatch [:prob2/kill! {:animator-id id
                                        :trace-ids trace-ids}])}]
      [:span (str main-component-name " (" filename ")")]]
     [:ul {:class "animator-list"}
      (if (seq elems)
        (map mk-trace-item elems)
        [:div]
        )]]))

(defn trace-selection-view []
  (let [raw-traces (subscribe [:traces])
        grouped (group-by :animator-id (vals @raw-traces))]
    [:div {:class "trace-selector"}
     [:ul {:class "trace-list"}
      (if (seq grouped)
        (map mk-animator-sublist grouped)
        [:div])]]))


(register-handler
 :prob2/kill!
 (comp rf/debug with-send)
 (fn [db [t m send!]]
   (send! [t m])
   db))

(register-handler
 :de.prob2.kernel/trace-removed
 (comp rf/debug decode)
 (fn [sdb [_ traces]]
   (reduce (fn [db uuid] (dissoc-in db [:traces uuid])) sdb traces)))
