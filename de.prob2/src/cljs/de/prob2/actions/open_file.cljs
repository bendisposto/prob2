(ns de.prob2.actions.open-file
  (:require [re-frame.core :as rf]
            [de.prob2.nw :as nw]))


(defn file-dialog []
  [:input {:style {:display "none"}
           :id "fileDialog"
           :type "file"
           :accept ".mch,.ref,.imp,.bum,.buc,.bcc,.bcm,.tla,.csp"
           :on-change (fn [e] (rf/dispatch [:open-file (-> e .-target .-value)]))}])

(rf/register-handler
 :open-file
 (fn [db [_ & file]]
   (if-not (seq file)
     (.click (.getElementById js/document "fileDialog"))
     (let [filename (first file)
           pages (get-in db [:ui :pages])
           label (last (clojure.string/split filename "/"))]
       (assoc-in db [:ui :pages] (conj pages {:id (inc (count pages)) :type :editor :label label :content {:file filename}}))))))

(rf/register-handler
 :start-animation
 (fn [db [_ filename]]
   (let [extension (last (re-find #".*\.(.*)" filename))
         text (nw/slurp filename)])
   db))
