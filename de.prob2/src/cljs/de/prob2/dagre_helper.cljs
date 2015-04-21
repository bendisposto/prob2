(ns de.prob2.dagre-helper
  (:require [taoensso.encore :as enc  :refer (logf log logp)]))

(defn add-node
 ([g node]
  (do (.setNode g
                (:name node)
                (clj->js (dissoc node :name))) g)))

(defn add-edge
  ([g edge]
   (do (.setEdge g
                 (:from edge)
                 (:to edge)
                 (clj->js edge)) g)))

(defn create-graph
  ([{:keys [nodes edges]}]
   (let [g0 (create-graph)
         g1 (reduce add-node g0 nodes)]
     (reduce add-edge g1 edges)))

  ([]
   (let [graph (js/global.dagre.graphlib.Graph.)]
     (.setGraph graph #js{})
     graph)))


(defn render [g] (do (.layout js/global.dagre g) g))

(defn node-names [g] (.concat #js[] (.nodes g)))
(defn nodes [g] (map #(.node g %) (.concat #js[] (.nodes g)))) ;; have
;; to concat for some weird javascript reason

(defn edge-names [g] (.concat #js[] (.edges g)))
(defn edges [g] (map #(.edge g %) (.concat #js[] (.edges g)))) 
 
(defn to-svg [g]
  [:svg {:width 500 :height 500}
    (.log js/console (node-names g))
   (for [n (node-names g)]
     (let [node (js->clj (.node g n))]
       (logp :n node)
            [:g [:rect {:width (or (node "width") 100)
                        :height (or (node "height") 50)
                        :x (node "x")
                        :y (node "y")
                        :style {:fill :white :stroke :black}}]
             [:text {:x (node "x") :y (node "y") :dy (if (node "height") (/ (node "height") 2) 25)
                     :dx 5} n]]))
   (for [e (edge-names g)]
     (let [edge (js->clj (.edge g e))
           _    (logp :edge edge)
           start (first (edge "points"))
           middle (second (edge "points"))
           end   (last  (edge "points"))
           _     (logp :sme start middle end)
           start-node (js->clj (.node g (edge "from")))
           w1         (start-node "width")
           h1         (start-node "height")
           end-node   (js->clj (.node g (edge "to")))
           w2         (end-node "width")
           h2         (end-node "height")]
       (.log js/console start-node)
       (.log js/console end-node)
       [:g [:line {:x1 (+ (start "x") (/ w1 2)) :y1 (+ (start "y") (/ h1 2))
-                  :x2 (+ (end   "x") (/ w2 2)) :y2 (+ (end   "y") (/ h2 2))
-                  :style {:stroke :black}}]
        [:text {:x (+ (middle "x") (/ w1 2)) :y (+  (middle "y") (/ h1 2)) } (or (edge "label") "")]]))])


(defn example []
  (let [g (-> (create-graph)
              (add-node {:name "me" :width 30 :height 24})
              (add-node {:name "you" :width 30 :height 24})
              (add-edge {:from "me" :to "you" :label "us"}) (render))]
    (to-svg g)))

