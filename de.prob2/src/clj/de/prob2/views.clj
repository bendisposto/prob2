(ns de.prob2.views
  (:require [de.prob2.kernel :as kernel]
            [de.prob2.sente :as sente]
            [com.stuartsierra.component :as component]))

(defn goto-position [{:keys [animations] :as prob} {:keys [index trace-id]}]
  (let [t (.getTrace animations trace-id)
        t' (.gotoPosition t index)]
    (.traceChange animations t')))



(defmulti dispatch-history sente/extract-action)
(defmethod dispatch-history :goto [prob {:keys [?data]}] (goto-position prob ?data))


(defrecord Views [prob] component/Lifecycle
           (start [this]
             (println "Starting views")
             (defmethod sente/handle-updates :history  [_ a] (dispatch-history prob a))
             this)
           (stop [this] (println "Stopping views") this))


(defn mk-views []
  (component/using (map->Views {}) [:prob]))
