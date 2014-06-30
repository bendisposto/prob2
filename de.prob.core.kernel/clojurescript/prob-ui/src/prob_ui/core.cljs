(ns prob-ui.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [prob-ui.syncclient :as sync]))

(enable-console-print!)

(sync/pp-state)

(defn mk-history-item [cn]
 (fn [app owner]
  (reify
    om/IRenderState
    (render-state [a b]
 	   (dom/li #js {:className cn} app)))))




(defn history-view [app owner]
  (reify
    om/IRenderState
    (render-state [a b]
      (println "params" (print-str a) (print-str b))
      (let [s (:state app)
  		  uuid (get-in s ["current-animation" "uuid"])
  		  past (get-in s [uuid "past"])
  		  futr (get-in s [uuid "future"])
  		  curr (first past)
  		  past (rest past)]
        (dom/div nil
          (dom/header #js {:id "header"}
            (dom/h1 nil "History")
            (apply dom/ul #js {:id "filters"} 
            	(concat 
            		(om/build-all (mk-history-item "future") futr)
            		[(dom/li #js {:className "current"} curr)]
            		(om/build-all (mk-history-item "past") past)
            		[(dom/li #js {:className "start"} "--root--")]
)
            	
            	)))))))

(om/root
  history-view
  sync/state
  {:target (. js/document (getElementById "app"))})
