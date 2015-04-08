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

(defn jens-init []
  (when-not system (go))
  (defn setup-basic-system [system]  (println "* Instantiate animations")
    (def ani (de.prob2.kernel/instantiate (:prob system) de.prob.statespace.Animations))
    (println "* Instantiate api")
    (def api (de.prob2.kernel/instantiate (:prob system) de.prob.scripting.Api))
    (println "* Load classical B model")
    (def m (.b_load api "/Users/bendisposto/joy/scheduler.mch"))
    (println "* Create classical B trace")
    (def t (de.prob.statespace.Trace. m))
    (println "* Load Event-B model")
    (def em (.eventb_load api "/Users/bendisposto/Downloads/rodin 2.8/xxx/Scheduler/Scheduler0.bum"))
    (def diss (.eventb_load api "/Users/bendisposto/Downloads/rodin 2.8/xxx/Landing_Dev_3/m4.bum"))
    (println "* Create Event-B trace")
    (def dt (de.prob.statespace.Trace. diss))
    (def et (de.prob.statespace.Trace. em))
    (println "* Adding classical B trace to UI")
    (.addNewAnimation ani t)
    (println "* Adding Event-B trace to UI")
    (.addNewAnimation ani dt)
    (.addNewAnimation ani et)
    (println "* Done."))
  (setup-basic-system system)
  (def x et)
  (use 'de.prob2.kernel)
  (doseq [_ (range 10)]
    (def x (.anyEvent x nil)))
  (doseq [_ (range 10)]
    (def dt (.anyEvent dt nil)))
  (.traceChange ani x))
