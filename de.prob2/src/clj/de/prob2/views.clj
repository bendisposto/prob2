(ns de.prob2.views
  (:require [compojure.core :refer [GET POST]]
            [de.prob2.handler :as handler]
            [de.prob2.kernel :as kernel]))


(defn goto-position [prob req]
  (println :req req)
  #_(let [id (read-string id)
        trace-id (java.util.UUID/fromString trace-id)
        ani (kernel/instantiate prob de.prob.statespace.AnimationSelector)
        t (.getTrace ani trace-id)
        t' (.gotoPosition t id)]
    (.traceChange ani t'))
  "ok")


(defn view-routes []
  (fn [sente prob]
    (let [default ((handler/default-routes) sente prob)]
      (concat [(POST "/history/goto" req
                     (goto-position prob req))]
              default))))
