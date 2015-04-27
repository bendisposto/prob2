(ns de.prob2.components.events
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.i18n :refer [i18n format]]))

(defn- disabled-if-not [test]
  (if-not test " disabled" ""))

(defn trace-fwd-back [id fwd? back?]
  [:div {:class "btn-group" :role "group" :aria-label "fwd-back"}
   [:button {:type "button"
             :class (str "btn btn-default " (disabled-if-not back?))
             :on-click #(rf/dispatch [:history/back id])}
    [:span {:class "glyphicon glyphicon-chevron-left"}]]
   [:button {:type "button"
             :class (str "btn btn-default " (disabled-if-not fwd?))
             :on-click #(rf/dispatch [:history/forward id])}
    [:span {:class "glyphicon glyphicon-chevron-right"}]]])


(defn random-panel [id sid rand-panel]
  (let [rand-id (str "random-n-select-" id)]
    [:div.panel.panel-default.random-panel.collapse {:id rand-panel}
     [:div {:class "panel-body random-menu list-group"}
      [:a.list-group-item {:on-click (fn [e] (rf/dispatch [:events/random {:state-id sid :trace-id id}]))} (i18n :execute-1-random)]
      [:a.list-group-item {:on-click (fn [e] (rf/dispatch [:events/random {:state-id sid :trace-id id :number 5}]))} (format :execute-n-random 5)]
      [:a.list-group-item {:on-click (fn [e] (rf/dispatch [:events/random {:state-id sid :trace-id id :number 10}]))} (format :execute-n-random 10)]
      [:form.list-group-item.form-inline
       [:div.form-group
        [:input.form-control {:type "text"
                              :id rand-id
                              :placeholder (i18n :n-random)}]
        [:button
         {:type "button"
          :class "btn btn-default random-button"
          :on-click (fn [e]
                      (let [input (.getElementById js/document rand-id)
                            value (js/parseInt (.-value input))]
                        (rf/dispatch
                         [:events/random
                          {:state-id sid
                           :trace-id id
                           :number value}])))}
         (i18n :execute)]]]]]))

(defn toolbar [sid id fwd? back? sort-c filter-fkt show-disabled?]
  (let [rand-panel (str "random-panel-" id)]
    [:div.events-toolbar
     [:form {:role "search"}
      [:input.form-control
       {:type "text"
        :placeholder (i18n :filter)
        :on-change
        (fn [e]
          (let [v (.-value (.-target e))]
            (reset! filter-fkt (partial
                                h/filter-input
                                v
                                identity
                                (fn [e nt] (when-not (empty? nt) e))
                                identity))))}]]
     [:div {:class "btn-toolbar" :role "toolbar" }
      [:div {:class "btn-group" :role "group" }
       [:button {:type "button"
                 :class "btn btn-default"
                 :data-toggle "collapse"
                 :data-target (str "#" rand-panel)}
        [:span {:class "glyphicon glyphicon-random"}]]]
      [trace-fwd-back id fwd? back?]
      [:div.btn-group {:role "group"}
       [:button.btn.btn-default {:type "button" :on-click #(swap! sort-c inc)} [:span.glyphicon.glyphicon-sort-by-attributes]]]
      [:div.btn-group {:role "group"}
       [:button.btn.btn-default {:type "button" :on-click #(swap! show-disabled? not)} [:span.glyphicon.glyphicon-eye-open]]]]
     [random-panel id sid rand-panel]



     ]))

(defn- single-event [state trace-id {:keys [id] :as item}]
  [:a.list-group-item
   {:key (h/fresh-id)
    :on-click #(rf/dispatch [:events/execute {:state-id state :trace-id trace-id :event-id id}])}
   (h/pp-transition item)])

(defn- disabled-event [event]
  [:div.list-group-item.list-group-item-danger
   {:key (h/fresh-id)} event])

(defn- mk-event-item [state trace-id show-disabled? [event items]]
  (let [ci (count items)
        fi (first items)]
    (cond (= 1 ci)
          (single-event state trace-id fi)
          (= 0 ci)
          (if show-disabled? (disabled-event event) "")
          :otherwise
          (let [egid (str "event-details-" (h/fresh-id))]

            [:div {:key egid}
             [:a.list-group-item
              {:data-toggle "collapse"
               :data-target (str "#" egid)}
              event
              [:span.badge.pull-right (count items)]]
             [:div
              {:class "list-group event-detail-dropdown collapse"
               :id egid}
              (doall (map (partial single-event state trace-id) items))]]))))


(defn- mk-events [sorted-events enabled-events]
  (for [e sorted-events]
    [e (get enabled-events e [])]))

(defn events-view [id]
  (let [show-disabled? (r/atom true)
        filter-fkt (r/atom (partial h/filter-input "" identity (fn [e nt] e) identity))
        sort-c (r/atom 0)]
    (fn []
      (let [trace (rf/subscribe [:trace id])
            {{sid :state} :current-state ts :out-transitions back? :back? fwd? :forward?} @trace
            model (rf/subscribe [:model id])
            main-component (:main-component-name @model)
            component-events  (map :name (get-in @model [:components main-component :events]))
            selected-events  (@filter-fkt component-events)
            sorted-events (if (odd? @sort-c) (sort selected-events) selected-events)
            enabled-events (group-by :name ts)]

        [:div {:class "events-view"}
         [toolbar sid id fwd? back? sort-c filter-fkt show-disabled?]
         [:div.events-list.list-group
          (doall
             (map
              (partial mk-event-item sid id @show-disabled?)
              (mk-events sorted-events enabled-events)))]]))))

(rf/register-handler :events/execute h/relay)
(rf/register-handler :history/back h/relay)
(rf/register-handler :history/forward h/relay)
(rf/register-handler :events/random h/relay)
