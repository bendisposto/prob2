(ns de.prob2.components.logo
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [ajax.core :refer [GET]]
            [taoensso.encore :as enc  :refer (logf log logp)]))

(defn put-picture [elem picture]
  (aset (.getElementById js/document elem) "innerHTML" picture))

(defn prob-logo []
  (r/create-class
   { :component-did-mount
    (fn [c]
      (GET "/img/logo_left.svg"
           {:handler
            (fn [resp] (put-picture "logo-left" resp))})
      (GET "/img/logo_center.svg"
           {:handler
            (fn [resp]
              (put-picture "logo-center" resp)
              )})
      (GET "/img/logo_right.svg"
           {:handler
            (fn [resp]
              (put-picture "logo-right" resp)
              )})
      (GET "/img/logo_empty.svg"
           {:handler
            (fn [resp]
              (put-picture "logo-leftb" resp)
              (put-picture "logo-centerb" resp)
              (put-picture "logo-rightb" resp)
              )}))

    :display-name "prob-logo"
    :reagent-render
    (fn [] [:div {:id "prob-logo" :style {:height 350}}
           [:div {:class "flip-container"}
            [:div {:class "flipper"}
             [:div {:class "front" :id "logo-left" }]
             [:div {:class "back" :id "logo-leftb" }]
             ]]
           [:div {:class "flip-container"}
            [:div {:class "flipper"}
             [:div {:class "front" :id "logo-center" }]
             [:div {:class "back" :id "logo-centerb" }]]]
           [:div {:class "flip-container"}
            [:div {:class "flipper"}
             [:div {:class "front" :id "logo-right" }]
             [:div {:class "back" :id "logo-rightb" }]]]])}))
