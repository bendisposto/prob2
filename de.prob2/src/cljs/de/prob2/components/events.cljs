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

(defn events-view [id]
  (let [filtered? (r/atom true)]
    (fn []
      (let [trace (rf/subscribe [:trace id])
            {{sid :state} :current-state ts :out-transitions} @trace
            ]
        [:div {:class "events-view"}
         [:ul {:class "events-list"}
          (map (partial mk-event-item sid id) ts)]]))))

(rf/register-handler :events/execute h/relay)
