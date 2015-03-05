(ns de.prob2.stateview
  (:require [de.prob2.kernel :as kernel]))

(defn- get-selector [prob]
  (kernel/instantiate prob de.prob.statespace.AnimationSelector))

(defn create-state-view [prob trace]
  (let [ani (get-selector prob)]
    (str  "My awesomer visualization for " trace " in " ani)))
