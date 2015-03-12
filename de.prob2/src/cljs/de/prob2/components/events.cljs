(ns de.prob2.components.events
  (:require [reagent.core :as reagent :refer [atom]]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf :refer
             [dispatch register-handler subscribe]]
            [de.prob2.helpers :refer [dissoc-in pp-transition fix-names fresh-id with-send decode]]))


(defn- mk-event-item [state trace-id {:keys [id] :as item}]
  ^{:key id}
  [:li
   [:a {:class "event-entry"
        :on-click #(dispatch [:events/execute {:state-id state :trace-id trace-id :event-id id}])}
    (pp-transition item)]])

(defn events-view []
  (let [filtered? (atom true)]
    (fn []
      (let [id (session/get :focused-uuid)
            traces (subscribe [:traces])
            {{ts :out-transitions state :id} :current} (get @traces id)]
        [:div {:class "events-view"}
         [:ul {:class "events-list"}
          (map (partial mk-event-item state id) ts)]]))))

(register-handler
 :events/execute
 (comp rf/debug with-send)
 (fn [db [t m send!]]
   (send! [t m])
   db))
