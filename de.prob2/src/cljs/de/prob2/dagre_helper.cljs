(ns de.prob2.dagre-helper)

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
                 (clj->js (dissoc edge :from :to))) g)))

(defn create-graph
  ([{:keys [nodes edges]}]
   (let [g0 (create-graph)
         g1 (reduce add-node g0 nodes)]
     (reduce add-edge g1 edges)))

  ([]
   (let [graph (js/dagre.graphlib.Graph.)]
     (.setGraph graph #js{})
     graph)))


(defn render [g] (do (.layout js/dagre g) g))

(defn nodes [g] (map #(.node g %) (.nodes g)))

(defn edges [g] (map #(.edge g %) (.edges g)))

(defn toSvg [g]
  [:svg {:width 500 :height 500}
   (for [n (nodes g)]
     (let [node (js->clj n)]
       [:g [:rect {:width (node "width")
                   :height (node "height")
                   :x (node "x")
                   :y (node "y")
                   :style {:fill :white :stroke :black}}]
        [:text {:x (node "x") :y (node "y") :dx 30 :dy 25} (node "label")
         ]]))
   (for [e (edges g)]
     (let [edge (js->clj e)
           start (first (edge "points"))
           middle (second (edge "points"))
           end   (last  (edge "points"))]
       [:g [:line {:x1 (+ 50 (start "x")) :y1 (+ 25 (start "y"))
                   :x2 (+ 50 (end   "x")) :y2 (+ 25 (end   "y"))
                   :style {:stroke :black}}]
        [:text {:x (middle "x") :y (middle "y") :dx 55 :dy 25} (edge "label")]]))])


(defn example []
  (let [g (-> (create-graph) (add-node {:name "me"}) (add-node (:name "you"))
              (add-edge {:from "me" :to "you" :label "us"}) (render))]
    (toSvg g)))
