(ns prob-ui.history
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn history-item [[id data] owner]
  (reify om/IRenderState
    (render-state [a b]
      (let [t (get data :type)
            n (get data :name)]
        (dom/li #js {:className t
                     :onClick (fn [e] (println "switch" id @data))}
                n))))) 

(defn history-view [app owner]
  (reify
    om/IInitState
    (init-state [_] {:reverse? false})

    om/IWillMount
    (will-mount [this] {:reverse? false})

    om/IRenderState
    (render-state [a {:keys [reverse?]}]
      (let [s (:state app)
            uuid (get-in s [:current-animation :uuid])
            hist0 (get-in s [uuid])
            hist1 (into [[:0 {:name "--root--" :type "start"}]]
                        (sort-by first hist0))
            hist2 (if reverse? (reverse hist1) hist1)
            ]

        (dom/div nil
                 (dom/span #js {:className "glyphicon glyphicon-sort pull-right"
                                :id "sort"
                                :onClick (fn [& e] (om/set-state! owner :reverse? (not reverse?)))
                                }
                           "")
                 (apply dom/ul #js {:id "history-list"}
                        (om/build-all history-item hist2)))))))
