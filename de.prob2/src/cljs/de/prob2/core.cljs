(ns de.prob2.core
  (:require-macros [de.prob2.macros :refer [remote-let]]
                   [cljs.core.async.macros :refer [go]])
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
            [de.prob2.components.formulabox :refer [formulabox]]
            [de.prob2.components.dot-view :refer [dot-view]]))

;; -------------------------
;; Views


(defn editor [id]
  (let [cm (atom nil)
        m (rf/subscribe [:model id])]
    (r/create-class
     {:component-did-mount
      (fn [c]
        (let [elem (.getDOMNode c)
              text (if @m (nw/slurp (:filename @m)) "")
              mirr (js/CodeMirror
                    elem
                    (clj->js
                     {:mode "b"
                      :lineNumbers true
                      :value text}))
              doc (.-doc mirr)]
          #_(.markText doc #js {:line 4 :ch 2} #js {:line 5 :ch 5} #js {:className "markymark"})
          (reset! cm mirr)))
      :reagent-render
      (fn []
        [:div {:class "panel panel-default"}
         [:div {:id (:main-component-name @m) :class "panel-body codemirror-panel"}
          ]])})))

(defn navigation [path]
  (into [:ol {:class "breadcrumb"}]
        (for [{:keys [name url active?]} path]
          (if active?
            [:li {:class "active"} name]
            [:li [:a {:href url} name]]))))

(defn home-page []
  [:div
   [navigation [{:name (i18n :animations) :url"#" :active? true}]]
   [trace-selection-view]])

(defn animation-view []
  (let [id (session/get :focused-uuid)
        m (rf/subscribe [:model id])]
    [:div {:id "h1"}
     [navigation [{:name (i18n :animations) :url"#"} {:name (:main-component-name @m) :url (str "#/trace/" id) :active? true}]]
     #_[dot-view "digraph simple { A->B }"]
     [editor id]
     #_[formulabox id]
     #_[formulabox id "zuck"[:label {:class "control-label" :for "zuck"} "Input:" ] nil]
     [:div
      [:div {:class "col-lg-4"} [state-view id]]
      [:span {:class "col-lg-4"} [events-view id]]
      [:span {:class "col-lg-4"} [history-view id]]]
     ]))


(defn machine-hierarchy []
  (let [id (session/get :focused-uuid)]
    [hierarchy-view id]))
