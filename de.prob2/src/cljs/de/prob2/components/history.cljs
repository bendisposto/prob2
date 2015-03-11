(ns de.prob2.components.history
  (:require [reagent.core :as reagent :refer [atom]]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf :refer
             [dispatch register-handler subscribe]]
            [de.prob2.helpers :refer [dissoc-in pp-transition fix-names fresh-id with-send decode]]))

(defn- mk-history-item [trace-id current {:keys [index] :as item}]
  ^{:key (str "h" index)}
  [:li
   [:a  {:class (str "history-item" (cond (= current index) " current "
                                          (< current index) " future "
                                          :default ""))
         :on-click
         #(dispatch [:history/goto {:trace-id trace-id :index index}])}
    (pp-transition item)]])

(defn history-view []
  (let [sort-order (atom identity)]
    (fn []
      (let [id (session/get :focused-uuid)
            traces (subscribe [:traces])
            t (get @traces id)
            h (cons {:name "-- uninitialized --"
                     :return-values []
                     :parameters []
                     :id -1
                     :index -1}
                    (map-indexed
                     (fn [index element] (assoc element :index index))
                     (:history t)))]
        [:div {:class "history-view"}
         [:div {:class "glyphicon glyphicon-sort pull-right"
                :id "sort-button"
                :on-click
                #(swap!
                  sort-order
                  (fn [f]
                    (get {identity reverse} f identity)))}]
         [:ul {:class "history-list"}
          (map (partial mk-history-item (:trace-id t) (:current-index t)) (@sort-order h))]
         ]))))

(register-handler
 :history/goto
 (comp rf/debug with-send)
 (fn [db [t m send!]]
   (send! [t m])
   db))
