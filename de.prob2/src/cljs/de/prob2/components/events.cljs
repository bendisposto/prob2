(ns de.prob2.components.events
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]))


(defn- mk-event-item [state trace-id {:keys [id] :as item}]
  ^{:key id}
  [:li
   [:a {:class "event-entry"
        :href (str "#/trace/" trace-id)
        :on-click #(rf/dispatch [:events/execute {:state-id state :trace-id trace-id :event-id id}])}
    (h/pp-transition item)]])

(defn- disabled-if-not [test]
  (if-not test " disabled" ""))

(defn events-view [id]
  (let [filtered? (r/atom true)]
    (fn []
      (let [trace (rf/subscribe [:trace id])
            {{sid :state} :current-state ts :out-transitions back? :back? fwd? :forward?} @trace]
        [:div {:class "events-view"}
         [:div {:class "btn-group"}
          [:button {:type "button"
                    :class (str "btn btn-default " (disabled-if-not back?))
                    :on-click #(rf/dispatch [:history/back id])}
           [:span {:class "glyphicon glyphicon-chevron-left"}]]
          [:button {:type "button"
                    :class (str "btn btn-default " (disabled-if-not fwd?))
                    :on-click #(rf/dispatch [:history/forward id])}
           [:span {:class "glyphicon glyphicon-chevron-right"}]]]
         [:ul {:class "events-list"}
          (map (partial mk-event-item sid id) ts)]]))))

(rf/register-handler :events/execute h/relay)
(rf/register-handler :history/back h/relay)
(rf/register-handler :history/forward h/relay)
