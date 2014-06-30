(ns prob-ui.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [prob-ui.syncclient :as sync]))

(enable-console-print!)


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
 
(om/root
  history-view
  sync/state
  {:target (. js/document (getElementById "app"))})

(when-let [t (. js/document (getElementById "blapp"))]
 (om/root
  history-view
  sync/state
  {:target t}))

