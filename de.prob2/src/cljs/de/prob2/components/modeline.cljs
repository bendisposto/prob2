(ns de.prob2.components.modeline
  (:require  [taoensso.encore :as enc  :refer (logf log logp)]
             [reagent.core :as r]
             [re-frame.core :as rf]
             [de.prob2.helpers :as h]))



(defn get-commands
  ([] [{:name "Open File" :desc "Opens a "}
       {:name "Shutdown Server" :action :kill :desc "Kills the ProB server. All running animations are killed and all unsaved data is discarded."}
       {:name "Foo" :desc "Foo ... obviously"}
       {:name "Bar"}])
  ([filter-string]
   (filter (fn [{n :name}] (.startsWith (.toLowerCase n) (.toLowerCase filter-string))) (get-commands))))

(defn make-cmd-entry [selected]
  (fn [index entry]
    (let [name (get entry :name)
          action (get entry :action (keyword (h/kebap-case name)))
          selected (if (= selected index) "selected-modeline-entry" "")]
      ^{:key name}
      [:li {:class selected}
       [:a {:on-click #(rf/dispatch [action])} name]])))

(defn modeline []
  (let [items (r/atom (get-commands))
        selected (r/atom 0)]
    (fn []
      [:div {:class "sidebar-nav"}
       [:input {:type "text"
                :id "modeline-search"
                :class "form-control"
                :placeholder "Search..."
                :role "search"
                :on-change (fn [e] (reset! items (get-commands (.-value (.-target e)))))
                :on-key-down (fn [e]
                               (let [k ({13 :enter 38 :up 40 :down} (.-which e))]
                                 (when k (rf/dispatch [:modeline :key k selected items])
                                       (.preventDefault e))))}]
       [:ul
        (map-indexed (make-cmd-entry @selected) @items)]])))

(defn modeline-key [kind index items]
  (let [cur-index @index
        items @items
        selected (get (vec items) cur-index)]
    (condp = kind
      :enter (let [action (get selected :action (keyword (h/kebap-case (:name selected))))]
               (rf/dispatch [action]))
      :up (when (< 0 cur-index) (swap! index dec))
      :down (when (< cur-index (dec (count items))) (swap! index inc)))
    (logp :keyboard-event kind :index cur-index :items items)))

(defn modeline-toggle []
  (let [wrapper (js/jQuery "#wrapper")
        visible? (.hasClass wrapper "toggled")
        searchbox (js/jQuery "#modeline-search")]
    (.toggleClass wrapper "toggled")
    (when visible? (.focus searchbox))))

(rf/register-handler
 :modeline
 (fn [db [_ cmd & args]]
   (condp = cmd
     :toggle (modeline-toggle)
     :key    (apply modeline-key args))
   db))
