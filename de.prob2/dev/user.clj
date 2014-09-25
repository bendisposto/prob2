(ns user
   (:require [com.stuartsierra.component :as component]
             [clojure.tools.namespace.repl :refer [refresh clear]]
             [de.prob2 :as app]))

(def system nil)

(defn init []
  (alter-var-root #'system
    (constantly (app/new-system {:host "localhost" :port 9001}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))


(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn force-reset []
  (clear)
  (reset))


(println "Devlopment Mode")
(println "Use (go) to start the system")
(println "Use (reset) to reload all sources and restart the system")

