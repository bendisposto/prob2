(ns prob-ui.groovy
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


(defn groovy-item [data owner]
  (reify om/IRenderState
    (render-state [a b]
      (println "D:" (pr-str data))
      (let [i (get data "in")
              o (get data "result")
              s (get data "status")]
          (dom/li #js {:className s}
                  (dom/p nil i) (dom/p nil o))))))

(defn groovy-view [app owner]
  (reify
    om/IInitState
    (init-state [_] {:active nil})

    om/IWillMount
    (will-mount [this] {:active nil})

    om/IRenderState
    (render-state [a {:keys [active]}]
      (let [g (get (:state app) "groovy")]
        (println "G" (pr-str g))
        (dom/div nil
                 (apply dom/ul #js {:id "groovy-list"}
                        (om/build-all groovy-item g)))))))
