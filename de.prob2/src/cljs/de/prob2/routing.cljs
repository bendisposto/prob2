(ns de.prob2.routing
  (:require-macros [reagent.ratom :as ra :refer [reaction]])
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.dom.dataset]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [goog.dom.query]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
            [re-frame.core :as rf]
            [de.prob2.jsapi]
            [de.prob2.subs]
            [markdown.core :as md]
            [de.prob2.components.logo :refer [prob-logo]]
            [de.prob2.core :as core]
            [de.prob2.nw :as nw]
            [de.prob2.helpers :as h :refer [mk-url]]
            [de.prob2.components.minibuffer :refer [render-minibuffer]]
            [de.prob2.actions.open-file :refer [file-dialog]]
            [de.prob2.i18n :refer [i18n language]]
            [de.prob2.menu]))

(defn preloader-waiting []
  [:div {:id "disconnected-screen"}
   [:h1 {:id "disconnected-msg"} (i18n :connecting)]
   [:img {:id "disconnected-img" :src "./img/disconnected.svg"}]])

(defn preloader-initializing []
  (.toggleClass (js/jQuery "#footer,#bg") "toggled")
  [:div])

(rf/register-handler :copy (fn [db _] (.execCommand js/document "copy") db))
(rf/register-handler :cut (fn [db _] (.execCommand js/document "cut") db))
(rf/register-handler :paste (fn [db _] (.execCommand js/document "paste") db))
(rf/register-handler :select-all (fn [db _] (.execCommand js/document "selectAll") db))

(rf/register-handler :prob2/start-animation h/relay)

(defn tab-title [_]
  (let [active (rf/subscribe [:active])]
    (fn [[idx {:keys [id label] :as entry}]]
      (let [class (if (= id @active) " active " "")]
        [:li {:key id
              :on-click #(rf/dispatch [:select-tab id])
              :class class
              :role "presentation"}
         [:a {:href (str "#tab" id)
              :role "tab"
              :data-toggle "tab"} label
          [:span.glyphicon.glyphicon-remove.glyph-fix.remove-glyph {:on-click #(rf/dispatch [:remove-tab id])}]]]))))

(rf/register-handler
 :select-tab
 (fn [db [_ id]] (assoc-in db [:ui :active] id)))

(rf/register-handler
 :remove-tab ;; TODO Handle last tab
 (fn [db [_  id]]
   (let [[before elem after] (partition-by #(= % id) (get-in db [:ui :pane]))
         removed (into [] (concat before after))
         active (count before)
         db' (assoc-in db [:ui :pane] removed)
         db'' (assoc-in db' [:ui :active] active)]
     (h/dissoc-in db'' [:ui :pages id]))))

(defn render-editor [_]
  (let [cm (atom nil)
        id (atom nil)
        content (atom nil)]
    (r/create-class
     {:component-did-mount
      (fn [c]
        (let [dom-element (.getElementById js/document @id)
              mirr (.fromTextArea
                    js/CodeMirror
                    dom-element #js {:mode "b"
                                     :autofocus true
                                     :lineWrapping true
                                     :lineNumbers true})
              doc (.-doc mirr)]
          (.setValue doc @content)
          (reset! cm mirr)))
      :component-did-update (fn [e]
                              (let [doc (.-doc @cm)]
                                (.setValue doc @content)))
      :reagent-render
      (fn [{ii :id {:keys [file]} :content}]
        (reset! content (nw/slurp file))
        (reset! id (str "editor-" ii))
        [:div {:key @id}
         [:textarea {:id @id
                     :autofocus "autofocus"
                     :defaultContent @content}]])})))

(defn render-md [_]
  (fn [{{:keys [file]} :content :as e}]
    (let [path (str "./doc/" (name @language) "/" file)
          text (nw/slurp path)
          html (md/md->html text)]
      [:div.padding-container {:dangerouslySetInnerHTML {:__html html}}])))

(defn render-default [{:keys [id type content]}]
  [:div [:h2 (str "Unknown View Type " type)]
   [:h3 "Content: "
    (prn-str content)]])



(defn render-page [_]
  (fn  [{t :type :as e}]
    (condp = t
      :editor [render-editor e]
      :md [render-md e]
      [render-default e])))


(defn render-app []
  (let [pages (rf/subscribe [:pages])
        width (rf/subscribe [:width])
        minibuffer (rf/subscribe [:minibuffer])
        height (rf/subscribe [:height])
        active-content (rf/subscribe [:active-content])]
    (r/create-class
     {:component-did-update
      (fn [_] (when @minibuffer
               (.focus (js/jQuery "#modeline-search"))))
      :reagent-render
      (fn []
        [:div {:role "tabpanel" :style {:height @height}}
         [:ul.nav.nav-tabs {:role "tablist"}
          (for [p @pages] ^{:key (first p)} [tab-title p])]
         [:div.tab-content {:style {:height (- @height 44 32)}} ;; navigation  footer
          [render-page @active-content]]
         [:div.footer "(c) 2015"]
         [:div#overlay
          {:class (if @minibuffer "" " hidden ")
           :style {:height (- @height 200)
                   :top 100
                   :width (- @width 200)
                   :left 100}}
          [render-minibuffer]]])})))

(defn top-panel []
  (let [init?  (rf/subscribe [:initialised?])
        ready?  (rf/subscribe [:encoding-set?])
        connected? (rf/subscribe [:connected?])]
    (fn []
      [:div
       [file-dialog]
       (if-not @connected? (preloader-waiting)
               (do (when (and @init? (not @ready?)) (rf/dispatch [:chsk/encoding nil]))
                   (if-not @ready?
                     (preloader-initializing)
                     (do  (rf/dispatch [:populate-menus])
                          [render-app]))))])))

(defn init-keybindings []
  (let [bs (partition 2 (nw/read-string (nw/slurp (str "./keybindings/" (nw/os-name) ".edn"))))]
    (doseq [[sc b] bs]
      (.add js/shortcut sc #(rf/dispatch [b])))))

(defn screen-size []
  {:height (.-innerHeight js/window)
   :width (.-innerWidth js/window)})

(defn init! []
  (init-keybindings)
  (set! (.-onresize js/window) #(rf/dispatch [:window-resize (screen-size)]))
  (rf/dispatch [:initialise-db (screen-size)])
  (r/render-component [top-panel] (.getElementById js/document "app")))
