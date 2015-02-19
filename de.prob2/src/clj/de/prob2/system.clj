(ns de.prob2.system
  (:require [de.prob2.server :as server]
            [de.prob2.handler :as handler]
            [de.prob2.kernel :as kernel]
            [com.stuartsierra.component :as component]))

;; Extending the routing can be done like this:
#_(defn my-routes []
    (fn [sente]
      (let [routes ((handler/default-routes) sente)]
        (concat [(compojure.core/GET "/barfoo/:id" [id] id)] routes))))

(defn mk-system [config-options]
  (let [{:keys [port]} config-options]
    (component/system-map
     :server (server/server port)
     :sente (handler/mk-sente)
     :handler (handler/mk-handler)
     :routes (handler/mk-routes (handler/default-routes))
     :prob (kernel/prob))))
