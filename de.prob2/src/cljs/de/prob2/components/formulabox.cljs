(ns de.prob2.components.formulabox
  (:require-macros [cljs.core.async.macros :as ma]
                   [de.prob2.macros :as m])
  (:require [cljs.core.async :as a]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [cljs-uuid.core :as uuid]
            [de.prob2.helpers :as h]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(defn parse [trace-id ratom]
  (m/remote-let [res (parse trace-id (:ascii @ratom))]
                (swap! ratom (fn [s]
                               (let [{status "status" unicode "unicode" ascii "ascii"} res]
                                 (if-not (= ascii (:ascii s))
                                   (assoc s :unicode nil)
                                   (assoc s
                                          :status ({true "" false "has-error"} status)
                                          :unicode unicode)))))))

(defn formulabox
  ([trace-id] (formulabox trace-id (gensym) nil nil))
  ([trace-id id bfor aftr]
   (let [ratom (r/atom {:status "" :ascii "" :unicode ""})
         c (a/chan)]
     (ma/go-loop [formula "" last-formula ""]
       (let [t (a/timeout 500)
             [v port] (a/alts! [c t] {:priority true})]
         (if (= port c)
           (do (a/close! t)
               (swap! ratom assoc :ascii v :unicode nil)
               (recur v last-formula))
           (do (when-not (= formula last-formula)
                 (if (empty? formula)
                   (reset! ratom {:status "" :ascii "" :unicode ""})
                   (do (parse trace-id ratom) (logp @ratom))))
               (recur formula formula)))))
     (fn []
       (let [s (:status @ratom)
             f0 (:unicode @ratom)
             f1 (get @ratom :ascii "")
             formula (if f0 f0 f1)]
         [:div
          [:div {:class (str "form-group " s)}
           (if bfor bfor "")
           [:input {:id id
                    :class "form-control"
                    :value formula
                    :on-change
                    (fn [e] (let [v (-> e .-target .-value)]
                             (ma/go (a/>! c v))))}]
           (if aftr aftr "")]])))))
