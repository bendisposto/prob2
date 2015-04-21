(ns de.prob2.views
  (:require [de.prob2.kernel :as kernel]
            [de.prob2.sente :as sente]
            [com.stuartsierra.component :as component]))

(defn goto-position [{:keys [animations] :as prob} {:keys [index trace-id]}]
  (let [t (.getTrace animations trace-id)
        t' (.gotoPosition t index)]
    (.traceChange animations t')))

(defn go-back [{:keys [animations] :as prob} trace-id]
  (let [t (.getTrace animations trace-id)
        t' (.back t)]
    (.traceChange animations t')))

(defn go-forward [{:keys [animations] :as prob} trace-id]
  (let [t (.getTrace animations trace-id)
        t' (.forward t)]
    (.traceChange animations t')))


(defn execute-event [{:keys [animations] :as prob} {:keys [state-id trace-id event-id]}]
  (let [t (.getTrace animations trace-id)
        s (.getCurrentState t)
        sid (.getId s)]
    (assert (= sid state-id) "Trying to execute event on a state taht is not the current state")
    (let [t' (.add t event-id)]
      (.traceChange animations t'))))



(defmulti dispatch-history sente/extract-action)
(defmethod dispatch-history :goto [prob {:keys [?data]}] (goto-position prob ?data))
(defmethod dispatch-history :back [prob {:keys [?data]}] (go-back prob ?data))
(defmethod dispatch-history :forward [prob {:keys [?data]}] (go-forward prob ?data))

(defmulti dispatch-events sente/extract-action)
(defmethod dispatch-events :execute [prob {:keys [?data]}]
  (execute-event prob ?data))

(defrecord Views [prob] component/Lifecycle
           (start [this]
             (println "Starting views")
             (defmethod sente/handle-updates :history  [_ a] (dispatch-history prob a))
             (defmethod sente/handle-updates :events  [_ a] (dispatch-events prob a))
             this)
           (stop [this] (println "Stopping views") this))


(defn mk-views []
  (component/using (map->Views {}) [:prob]))
