(ns de.prob2.components.modeline
  (:require  [taoensso.encore :as enc  :refer (logf log logp)]
             [re-frame.core :as rf]))


(defn modeline []
  [:ul {:class "sidebar-nav"}
   [:li [:a {:href "#"} "Foo"]]
   [:li [:a {:href "#"} "Bar"]]])
