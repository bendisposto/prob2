(ns de.prob2.components.formulabox
  (:require-macros [cljs.core.async.macros :as m])
  (:require [cljs.core.async :as a]
            [reagent.core :as r]
            [de.prob2.helpers :as h]
            [taoensso.encore :as enc  :refer (logf log logp)]))

                      

(defn formulabox [trace-id]
  (let [error (r/atom "formula-ok")
        c (a/chan)]
    (m/go-loop [formula "" last-formula ""]
      (let [t (a/timeout 500)
            [v port] (a/alts! [c t])]
        (if (= port c)
          (do (a/close! t)
              (recur v last-formula))
          (do (when-not (= formula last-formula)
                (h/remote-call
                 (fn [res]
                   (let [cls ({true "formula-ok" false "has-error"} res)]
                     (logp :new-class cls)
                     (swap! error (fn [x] (logp :old x) cls))
                     (logp @error)))
                 "parse"
                 trace-id
                 formula))
              (recur formula formula)))))
    (fn []
      [:div {:class (str "form-group " @error ) }
       [:input {:class "form-control" :on-change
                (fn [e] (let [v (-> e .-target .-value)]
                         (m/go (a/>! c v))))}]])))
