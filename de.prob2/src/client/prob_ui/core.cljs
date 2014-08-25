(ns prob-ui.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.dom.query]
            [goog.array]
            [goog.dom.dataset]
            [prob-ui.syncclient :as sync]
            [prob-ui.history :as history]
            [prob-ui.groovy :as groovy]))

(enable-console-print!)

(def components {"history" history/history-view
                 "groovy" groovy/groovy-view}) 



(defn ^:export register
  ([component] (register component component []))
  ([component gui-id settings]
     (println settings)
     (when-let [t (. js/document (getElementById gui-id))]
       (om/root
        (get components component)
        sync/state
        {:target t})))) 

(defn setup-components []
  (let [cs (goog.dom.query "div[data-type]")]
    (doseq [c (array-seq  cs 0)]
      (register (goog.dom.dataset/get c "type") (.-id c) (js->clj (goog.dom.dataset/getAll c))))))

(defn init-update-loop [time]
  (.setInterval js/window sync/get-updates time))

(defn onload [& args]
  (setup-components)
  (init-update-loop 500))

(set! (.-onload js/window) onload)
