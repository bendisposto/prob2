(ns de.prob2.views
  (:require [de.prob2.kernel :as kernel]
            [de.prob2.sente :as sente]
            [com.stuartsierra.component :as component]))

(defn goto-position [prob {:keys [index trace-id]}]
  (let [ani (kernel/instantiate prob de.prob.statespace.AnimationSelector)
        t (.getTrace ani trace-id)
        t' (.gotoPosition t index)]
    (.traceChange ani t')))



(defmulti dispatch-history sente/extract-action)
(defmethod dispatch-history :goto [prob {:keys [?data]} _] (goto-position prob ?data))


(defrecord Views [prob] component/Lifecycle
           (start [this]
             (println "Starting views")
             (defmethod sente/handle-updates :history  [a b] (dispatch-history prob a b))
             this)
           (stop [this] (println "Stopping views") this))


(defn mk-views []
  (component/using (map->Views {}) [:prob]))
