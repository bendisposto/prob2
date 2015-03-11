(ns de.prob2.components.history)

(defn- mk-history-item [trace-id current {:keys [index] :as item}]
  ^{:key (str "h" index)}
  [:li
   [:a  {:class (str "history-item" (cond (= current index) " current "
                                          (< current index) " future "
                                          :default ""))
         :on-click (fn [_] (client/send! :history/goto {:trace-id trace-id :index index}))}
    (pp-transition item)]])

(defn history-view []
  (let [sort-order (atom identity)]
    (fn []
      (let [id (session/get :focused-uuid)
            t (get-in @state [:traces id])
            h (cons {:name "-- uninitialized --" :return-values [] :parameters [] :id -1 :index -1} (map-indexed (fn [index element] (assoc element :index index)) (:history t)))]
        [:div {:class "history-view"}
         [:div {:class "glyphicon glyphicon-sort pull-right"
                :id "sort-button"
                :on-click (fn [_] (swap! sort-order
                                        (fn [f] (get {identity reverse} f identity))))}]
         [:ul {:class "history-list"}
          (map (partial mk-history-item (:trace-id t) (:current-index t)) (@sort-order h))]
         ]))))
