(ns de.prob2.components.modeline
  (:require  [taoensso.encore :as enc  :refer (logf log logp)]
             [re-frame.core :as rf]
             [de.prob2.helpers :as h]))



(defn get-commands
  ([] [{:name "Open File" :desc "Opens a "}
       {:name "Shutdown Server" :action :kill :desc "Kills the ProB server. All running animations are killed and all unsaved data is discarded."}
       {:name "Foo" :desc "Foo ... obviously"}
       {:name "Bar"}])
  ([filter-string] (get-commands)))

(defn make-cmd-entry [entry]
  (let [name (get entry :name)
        action (get entry :action (keyword (h/kebap-case name)))]
    ^{:key name}
    [:li [:a {:on-click #(rf/dispatch [action])} name]]))

(defn modeline []
  (fn []
    (let [cmd-list (get-commands)]
      [:div {:class "sidebar-nav"}
       [:form 
        [:input {:type "text" :id "modeline-search" :class "form-control" :placeholder "Search..." :role "search"}]]
       [:ul 
        (map make-cmd-entry cmd-list)]])))


(rf/register-handler
 :modeline
 (fn [db _]
   (let [wrapper (js/jQuery "#wrapper")
         visible? (.hasClass wrapper "toggled")
         searchbox (js/jQuery "#modeline-search")]
     (.toggleClass wrapper "toggled")
     (when visible? (.focus searchbox))
     )
   db))


