(ns de.prob2 
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response])
  (:import [de.prob Main] [de.prob.scripting ScriptEngineProvider])) 
 
(def injector (Main/getInjector))

(defn get-instance [c]
  (.getInstance injector c))

(def groovy-instance
  (let [sep (get-instance ScriptEngineProvider)]
    (.get sep)))

(defn groovy [input]
  (let [c (StringBuffer.)]
    (try
      (let [[result output] (.eval groovy-instance input c)]
        {:result (str result) :output output}
        )
      (catch Exception e {:result nil :output "" :error (str e)}))))
