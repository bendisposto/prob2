(ns de.prob2.dagre-helper)

(defn create-graph []
  (let [graph (js/dagre.graphlib.Graph.)]
    (.setGraph graph #js{})
    graph))

(defn add-node
  ([g name] (add-node g name {:label name :width 100 :height 50}))
  ([g name traits]
     (do (.setNode g name (clj->js traits)) g)))

(defn add-edge
  ([g from to] (add-edge g from to {}))
  ([g from to traits]
     (do (.setEdge g from to (clj->js traits)) g)))

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
  (let [g (-> (create-graph) (add-node "me") (add-node "you")
              (add-edge "me" "you" {:label "us"}) (render))]
    (toSvg g)))
