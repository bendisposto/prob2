(ns de.prob2.components.modeline
  (:require  [taoensso.encore :as enc  :refer (logf log logp)]
             [reagent.core :as r]
             [re-frame.core :as rf]
             [de.prob2.helpers :as h]
             [de.prob2.i18n :refer [i18n]]
             [goog.style :as style]))


(defn pattern-match [regex]
  (fn [e]
    (let [n (:name e)
          f (next (re-find regex n))
          nt (map-indexed (fn [i x] (if (= 1 (mod i 2)) [:u x] x )) f)]
      (if-not (empty? nt) (assoc e :display nt) e))))

(defn get-commands
  ([] (into  [{:name (i18n :open-file) :action :open-file :desc "Opens a "}
              {:name "Shutdown Server" :action :kill :desc "Kills the ProB server. All running animations are killed and all unsaved data is discarded."}
              {:name "Foo" :desc "Foo ... obviously"}
              {:name "Bar"}]
             (for [e (range 10)] {:name (str "Some Command " e)})))
  ([filter-string]
   (let [s (clojure.string/split filter-string #"\s+")
         re (str "(?i)(.*)" (clojure.string/join "(.*)" (map #(str "(" % ")") s)) "(.*)")
         filter-regex (re-pattern re)
         all-commands (get-commands)
         transformed (map (pattern-match filter-regex) all-commands)]
     (filter #(:display %) transformed))))

(defn make-cmd-entry [selected]
  (fn [index entry]
    (let [name (get entry :name)
          action (get entry :action (keyword (h/kebap-case name)))
          selected (if (= selected index) "active" "")]
      (into [:a {:key name
                 :id (str "modeline-entry" index)
                 :class (str "list-group-item " selected)
                 :on-click #(rf/dispatch [action])}] (:display entry)))))

(defn modeline []
  (let [items (r/atom {:elems (get-commands "") :index 0})]
    (fn []
      [:div {:id "sidebar-wrapper" :on-click (fn [e] (.focus (js/jQuery "#modeline-search")))}
       [:div {:class "container-fluid"}
        [:div {:class "row"}
         [:div {:class "col-lg-12"}
          [:div {:class "sidebar-nav"}
           [:div {:id "scroll-anchor"}]
           [:input {:type "text"
                    :id "modeline-search"
                    :class "form-control"
                    :placeholder (i18n :search)
                    :role "search"
                    :on-change (fn [e] (reset! items {:index 0 :elems (get-commands (.-value (.-target e)))}))
                    :on-key-down (fn [e]
                                   (let [k ({13 :enter 38 :up 40 :down} (.-which e))]
                                     (when k (rf/dispatch [:modeline :key k items])
                                           (.preventDefault e))))}]
           [:div {:class "list-group"}
            (let [{selected :index elems :elems} @items]
              (map-indexed (make-cmd-entry selected) elems))]]]]]])))

(defn modeline-toggle []
  (let [wrapper (js/jQuery "#wrapper")
        visible? (.hasClass wrapper "toggled")
        searchbox (js/jQuery "#modeline-search")]
    (.toggleClass wrapper "toggled")
    (when visible? (.focus searchbox))))

(defn modeline-key [kind ratom]
  (let [cur-index (:index @ratom)
        items (:elems @ratom)
        selected (get (vec items) cur-index)]
    (condp = kind
      :enter (let [action (get selected :action ::missing-action)]
               (rf/dispatch [action])
               (modeline-toggle))
      :up (if (< 0 cur-index)
            (do
              (style/scrollIntoContainerView
               (.getElementById js/document (str "modeline-entry" (dec cur-index)))
               (.getElementById js/document "sidebar-wrapper"))
              (swap! ratom update-in [:index] dec))
            (style/scrollIntoContainerView
             (.getElementById js/document "scroll-anchor")
             (.getElementById js/document "sidebar-wrapper")))
      :down (when (< cur-index (dec (count items)))
              (style/scrollIntoContainerView
               (.getElementById js/document (str "modeline-entry" (inc cur-index)))
               (.getElementById js/document "sidebar-wrapper"))
              (swap! ratom update-in [:index] inc)))))


(rf/register-handler
 ::missing-action
 (fn [db _] (js/alert "Missing action") db))


(rf/register-handler
 :modeline
 (fn [db [_ cmd & args]]
   (condp = cmd
     :toggle (modeline-toggle)
     :key    (apply modeline-key args))
   db))
