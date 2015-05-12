(ns de.prob2.components.minibuffer
  (:require  [taoensso.encore :as enc  :refer (logf log logp)]
             [reagent.core :as r]
             [re-frame.core :as rf]
             [de.prob2.helpers :as h]
             [de.prob2.i18n :refer [i18n]]
             [goog.style :as style]
             [de.prob2.menu :refer [menu-data]]))

(defn extract-menu [akku menu]
                                        ; (logp :enter akku menu)
  (cond
    (contains? menu :submenu) (extract-menu akku (:submenu menu))
    (contains? menu :action) (let [a (:action menu)]
                               (conj akku {:action a :name (str "Menu: " (i18n a))}))
    (and  (not (map? menu)) (seqable? menu)) (reduce extract-menu akku menu)
    :else akku ))


(defn all-commands []
  (let [md (menu-data)
        tf (extract-menu [] md)]
    tf))


(defn make-cmd-entry [selected]
  (fn [index entry]
    (let [name (get entry :name)
          action (get entry :action (keyword (h/kebap-case name)))
          selected (if (= selected index) "active" "")]
      (into [:a {:key name
                 :id (str "modeline-entry" index)
                 :class (str "list-group-item " selected)
                 :on-click #(rf/dispatch [action])}] (:display entry)))))

(defn render-minibuffer []
  (let [items (r/atom {:elems
                       (h/filter-input
                        ""
                        :name
                        (fn [x nt]
                          (if-not (empty? nt)
                            (assoc x :display nt)
                            x))
                        (fn [e] (get e :display))
                        (all-commands))
                       :index 0})]
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
                    :on-change
                    (fn [e] (reset! items
                                   {:index 0
                                    :elems (h/filter-input
                                            (.-value (.-target e))
                                            :name
                                            (fn [x nt]
                                              (if-not (empty? nt)
                                                (assoc x :display nt)
                                                x))
                                            (fn [e] (get e :display))
                                            (all-commands))}))
                    :on-key-down (fn [e]
                                   (let [k ({13 :enter 38 :up 40 :down} (.-which e))]
                                     (when k (rf/dispatch [:minibuffer :key k items])
                                           (.preventDefault e))))}]
           [:div {:class "list-group"}
            (let [{selected :index elems :elems} @items]
              (map-indexed (make-cmd-entry selected) elems))]]]]]])))


(defn minibuffer-key [kind ratom]
  (let [cur-index (:index @ratom)
        items (:elems @ratom)
        selected (get (vec items) cur-index)]
    (condp = kind
      :enter (let [action (get selected :action ::missing-action)]
               (rf/dispatch [action])
               (rf/dispatch [:minibuffer]))
      :up (if (< 0 cur-index)
            (do
              (style/scrollIntoContainerView
               (.getElementById js/document (str "modeline-entry" (dec cur-index)))
               (.getElementById js/document "overlay"))
              (swap! ratom update-in [:index] dec))
            (style/scrollIntoContainerView
             (.getElementById js/document "scroll-anchor")
             (.getElementById js/document "overlay")))
      :down (when (< cur-index (dec (count items)))
              (style/scrollIntoContainerView
               (.getElementById js/document (str "modeline-entry" (inc cur-index)))
               (.getElementById js/document "overlay"))
              (swap! ratom update-in [:index] inc)))))


(rf/register-handler
 ::missing-action
 (fn [db _] (js/alert "Missing action") db))

(rf/register-handler
 :minibuffer
 (fn [db [_ cmd & args]]
   (cond
     (or (not args) (= :toggle cmd)) (update-in db [:ui :show-minibuffer] not)
     (= :key cmd) (do (apply minibuffer-key args) db)
     :otherwise (do (logp :no-handler-defined cmd args) db))))
