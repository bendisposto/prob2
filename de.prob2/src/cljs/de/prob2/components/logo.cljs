(ns de.prob2.components.logo
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [ajax.core :refer [GET]]
            [taoensso.encore :as enc  :refer (logf log logp)]))

(defn prob-logo []
  (r/create-class
   { :component-did-mount
    (fn [c]
      (let [elem (.getDOMNode c)]
        (GET "/img/logo.svg"
             {:handler
              (fn [resp]
                (aset elem "innerHTML" resp))})))
    :display-name "prob-logo"
    :reagent-render (fn [] [:div {:id "prob-logo"}])}))
