(ns de.prob2.components.history
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.i18n :refer [i18n]]))

(defn- mk-history-item [trace-id current {:keys [index] :as item}]
  ^{:key (str "h" index)}
  [:li
   [:a  {:class (str "history-item" (cond (= current index) " current "
                                          (< current index) " future "
                                          :default ""))
         :on-click
         #(rf/dispatch [:history/goto {:trace-id trace-id :index index}])}
    (h/pp-transition item)]])

(defn history-view [id]
  (let [sort-order (r/atom identity)]
    (fn []
      (let [t (rf/subscribe [:trace id])
            h (cons {:name (i18n :not-initialized)
                     :return-values []
                     :parameters []
                     :id -1
                     :index -1}
                    (map-indexed
                     (fn [index element] (assoc element :index index))
                     (:transitions @t)))]
        [:div {:class "history-view"}
         [:div {:class "glyphicon glyphicon-sort pull-right"
                :id "sort-button"
                :on-click
                #(swap!
                  sort-order
                  (fn [f]
                    (get {identity reverse} f identity)))}]
         [:ul {:class "history-list"}
          (map (partial mk-history-item (:trace-id @t) (:current-index @t)) (@sort-order h))]
         ]))))

(rf/register-handler :history/goto h/relay)
