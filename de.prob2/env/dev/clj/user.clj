(ns user
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh)]
            [de.prob2.system :as sys]))

(def system nil)

(defn init []
  (alter-var-root
   #'system
   (constantly
    (sys/mk-system {:port 3000
                   ; :ip "0.0.0.0"
                    }))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (refresh :after 'user/go))

(defn restart []
  (stop)
  (reset))
