(ns de.prob2.actions.open-file
  (:require [re-frame.core :as rf]
            [de.prob2.nw :as nw]
            [de.prob2.helpers :as h]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(defn file-dialog []
  (fn []
    [:input {:style {:display "none"}
             :id "fileDialog"
             :type "file"
             :value nil
             :accept ".mch,.ref,.imp,.bum,.buc,.bcc,.bcm,.tla,.csp"
             :on-change (fn [e] (rf/dispatch [:open-file (-> e .-target .-value)]))}]))

(rf/register-handler
 :open-file
 (fn [db [_ & file]]
   (if-not (seq file)
     (.click (.getElementById js/document "fileDialog"))
     (let [filename (first file)
           id (h/fresh-id)
           label (last (clojure.string/split filename "/"))
           entry {:id id
                  :type :editor
                  :label label
                  :content {:file filename}}
           db' (update-in db [:ui :pane] conj id)
           db'' (assoc-in db' [:ui :pages id] entry)]
       (logp :1 (:ui db)
             :2 (:ui db')
             :3 (:ui db''))
       db''))))

(rf/register-handler
 :start-animation
 (fn [db [_ filename]]
   (let [extension (last (re-find #".*\.(.*)" filename))
         text (nw/slurp filename)])
   db))
