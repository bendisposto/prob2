(ns de.prob2.components.navbar
  (:require [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]))


(defn toggle []
  [:button {:type "button"
            :class "navbar-toggle collapsed"
            :data-toggle "collapse"
            :data-target "#navbar"
            :aria-expanded "false"
            :aria-controls "navbar"}
   [:span {:class "sr-only"}]
   [:span {:class "icon-bar"}]
   [:span {:class "icon-bar"}]
   [:span {:class "icon-bar"}]])

(defn bug-report [f] (logp :c))

(set! (.-triggerFunction (.-ATL_JQ_PAGE_PROPS js/window)) bug-report)

(rf/register-handler
 :bugreport
 (fn [db _] db))

(defn navigation []
  [:nav {:class "navbar navbar-default navbar-fixed-top"}
   [:div {:class "container"}
    [:div {:class "navbar-header"}
     [toggle]]
    [:div {:id "navbar"
           :class "navbar-collapse collapse"}
     [:ul {:class "nav navbar-nav"}
      [:li {:class "dropdown"}
       [:a {:href "#" :class "dropdown-toggle" :data-toggle "dropdown" :role "button" :aria-expanded "false"} "File" [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu" :role "menu"}
        [:li [:a {:href "#"} "Open"]]
        [:li [:a {:href "#"} "New"]]
        [:li {:class "divider"}]
        [:li [:a {:href "#" :on-click #(rf/dispatch [:bugreport])} "Report a bug"]]
        ]]]]]]
  )


