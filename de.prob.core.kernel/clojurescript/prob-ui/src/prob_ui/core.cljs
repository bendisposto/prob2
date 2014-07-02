(ns prob-ui.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.dom.query]
            [goog.array]
            [goog.dom.dataset]
            [prob-ui.syncclient :as sync]))

(enable-console-print!)

(extend-type js/NodeList
  ISeqable
  (-seq [array] (array-seq array 0)))


                                        ;(def jquery (js* "$"))



(defn history-item [[_ app] owner]
  (reify
    om/IRenderState
    (render-state [a b]
      (dom/li #js {:className (get app "type")} (get app "name")))))

(defn history-view [app owner]
  (reify
    om/IRenderState
    (render-state [a b]
      (let [s (:state app)
            uuid (get-in s ["current-animation" "uuid"])
            hist1 (into [["0" {"name" "--root--" "type" "start"}]]
                        (sort-by first (get-in s [uuid])))
            history (if (:history-reverse (:localstate app)) (reverse hist1) hist1)
            ]
        (dom/div nil
                 (dom/button
                  #js {:onClick
                       (fn [] (let [ls (:history-reverse (:localstate @sync/state))]
                               (swap! sync/state assoc-in [:localstate :history-reverse] (not ls))))
                       }
                  "Reverse Order")
                 (apply dom/ul #js {:id "filters"}
                        (om/build-all history-item history)))))))



(comment (when-let [t (. js/document (getElementById "app"))]
           (om/root
            history-view
            sync/state
            {:target t})))

(def components {"history" history-view})

(defn ^:export register
  ([component] (register component component []))
  ([component gui-id settings]
     (println settings)
     (when-let [t (. js/document (getElementById gui-id))]
       (om/root
        (get components component)
        sync/state
        {:target t})))) 


(let [cs (goog.dom.query "div[data-type]")]
  (doseq [c cs]
    (register (goog.dom.dataset/get c "type") (.-id c) (js->clj (goog.dom.dataset/getAll c)))))

