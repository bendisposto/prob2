(ns de.prob2.core
  (:require-macros [de.prob2.macros :refer [remote-let]]
                   [cljs.core.async.macros :refer [go]]
                   [reagent.ratom :as ra :refer [reaction]])
  (:require [cljs.core.async :as async]
            [reagent.core :as r]

            [de.prob2.client :as client]
            [re-frame.core :as rf]

            [de.prob2.nw :as nw]
            [taoensso.encore :as enc  :refer (logf log logp)]

            [de.prob2.helpers :as h]
            [reagent.session :as session]
            [de.prob2.i18n :refer [i18n]]

            [de.prob2.components.trace-selection :refer [trace-selection-view]]
            [de.prob2.components.state-inspector :refer [state-view]]
            [de.prob2.components.history :refer [history-view]]
            [de.prob2.components.hierarchy :refer [hierarchy-view]]
            [de.prob2.components.events :refer [events-view]]
            [de.prob2.components.formulabox :refer [formulabox]]))

;; -------------------------
;; Views


(defn navigation [path]
  (into [:ol {:class "breadcrumb"}]
        (for [{:keys [name url active?]} path]
          (if active?
            [:li {:class "active"} name]
            [:li [:a {:href url} name]]))))

(defn editor [id]
  (let [cm (atom nil)
        editor-id (str "editor" (h/fresh-id))
        m (rf/subscribe [:model id])
        filename (reaction (:filename @m))
        content (reaction (if @filename (nw/slurp @filename) ""))]
    (r/create-class
     {:component-did-mount
      (fn [c] (logp :mount @filename)
        (let [dom-element (.getElementById js/document editor-id)
              mirr (.fromTextArea
                    js/CodeMirror
                    dom-element #js {:mode "b"
                                     :lineWrapping true
                                     :lineNumbers true})]
          (reset! cm mirr)))
      :component-did-update (fn [e]
                              (let [doc (.-doc @cm)]
                                (.setValue doc @content)))
      :reagent-render
      (fn []
        (logp :render @filename)
        [:div {:class "coll-lg-12"}
         [:div {:class "panel panel-default"}
          [:div {:class "panel-body codemirror-panel"}
           [:textarea {:id editor-id :defaultValue @content}]]]])})))

(defn home-page []
  [:div
   [navigation [{:name (i18n :animations) :url"#" :active? true}]]
   [trace-selection-view]])

(defn animation-view []
  (let [id (session/get :focused-uuid)
        m (rf/subscribe [:model id])]
    [:div {:id "h1"}
     [navigation [{:name (i18n :animations) :url"#"} {:name (:main-component-name @m) :url (str "#/trace/" id) :active? true}]]
     [editor id]

     #_[formulabox id]
     #_[formulabox id "zuck"[:label {:class "control-label" :for "zuck"} "Input:" ] nil]
     [:div
      [:div {:class "col-lg-4"} [state-view id]]
      [:span {:class "col-lg-4"} [events-view id]]
      [:span {:class "col-lg-4"} [history-view id]]]
     #_[:div  {:class "col-lg-12"} [hierarchy-view id]]
     ]))
