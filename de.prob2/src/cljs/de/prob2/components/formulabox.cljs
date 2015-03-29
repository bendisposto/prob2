(ns de.prob2.components.formulabox
  (:require-macros [cljs.core.async.macros :as ma]
                   [de.prob2.macros :as m])
  (:require [cljs.core.async :as a]
            [reagent.core :as r]
            [de.prob2.helpers :as h]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(defn parse [trace-id formula ratom]
  (m/remote-let [res (parse trace-id formula)]
       (reset! ratom ({true "formula-ok" false "has-error"} res))))
 
(defn formulabox
  ([trace-id] (formulabox trace-id (gensym) nil nil))
  ([trace-id id bfor aftr]
   (let [error (r/atom "formula-ok")
         c (a/chan)]
     (ma/go-loop [formula "" last-formula ""]
       (let [t (a/timeout 500)
             [v port] (a/alts! [c t])]
         (if (= port c)
           (do (a/close! t)
               (recur v last-formula))
           (do (when-not (= formula last-formula)
                 (if (empty? formula)
                   (reset! error "formula-ok")
                   (parse trace-id formula error)))
               (recur formula formula)))))
     (fn []
       [:div {:class (str "form-group " @error)}
        (if bfor bfor "")
        [:input {:id id :class "form-control" :on-change
                 (fn [e] (let [v (-> e .-target .-value)]
                          (ma/go (a/>! c v))))}]
        (if aftr aftr "")]))))
