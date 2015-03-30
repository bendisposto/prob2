(ns de.prob2.components.formulabox
  (:require-macros [cljs.core.async.macros :as ma]
                   [de.prob2.macros :as m])
  (:require [cljs.core.async :as a]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [cljs-uuid.core :as uuid]
            [de.prob2.helpers :as h]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(defn get-input [state]
  (let [unicode (get (:parser-result state) "unicode")
        ascii (get (:parser-result state) "ascii")
        input (get state :input)]
    (or unicode ascii input)))

(defn syntax-class [state]
  (if (empty? (:input state)) "" ({true "" false "has-error"} (get-in state [:parser-result "status"]))))



(defn formulabox [trace-id id]
  (let [local-state (uuid/make-random)
        ]
    (r/create-class
     {:component-did-mount
      (fn [c]
        (let [dom-element (.getDOMNode c)]
          (rf/dispatch [::create-box local-state {:trace-id trace-id
                                                  :component-id id
                                                  :input ""
                                                  :status ""
                                                  :dom-element dom-element}]))
        (h/subs->handler ::box-change
                         [:state-path [:local-state local-state]]))
      :component-will-unmount
      (fn [c]
        (rf/dispatch [::destroy-box local-state]))
      :reagent-render (fn []
                        [:div {
                               :class "form-group"
                               :on-change (fn [e]
                                            (let  [v (-> e .-target .-value)
                                                   ss (-> e .-target .-selectionStart)
                                                   se (-> e .-target .-selectionEnd)]
                                              (rf/dispatch [::input local-state v ss se])))}
                         [:input {:id id :class "form-control"}]])})))

(rf/register-handler
 ::create-box
 (fn [db [_ local-state content]]
   (assoc-in db [:local-state local-state] content)))

(rf/register-handler
 ::input
 (fn [db [_ ls v ss se]]
   (h/remote-call [:local-state ls :parser-result] "parse" (get-in db [:local-state ls :trace-id]) v)
   (let [db (assoc-in db [:local-state ls :input] v)
         db (assoc-in db [:local-state ls :sel-start] ss)
         db (assoc-in db [:local-state ls :sel-end] se)]
     db)))

(rf/register-handler
 ::destroy-box
 (fn [db [_ local-state]]
   (h/dissoc-in db [:local-state local-state])))

(rf/register-handler
 ::box-change
 (fn [db [_ {:keys [component-id] :as state}]]
   (logp component-id state)
   (let [elem (.getElementById js/document component-id)]
     (when component-id
       (set! (.-value elem) (get-input state))
       (set! (.-selectionStart elem) (:sel-start state))
       (set! (.-selectionEnd elem) (:sel-end state))
       (set! (.-className (.-parentElement elem)) (str "form-group " (syntax-class state)))))
   db))
