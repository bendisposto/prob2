(ns de.prob2.actions.open-file
  (:require [re-frame.core :as rf]))


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
           extension (last (re-find #".*\.(.*)" filename))]
       (rf/dispatch [:prob2/start-animation [filename extension]])))
   db))
