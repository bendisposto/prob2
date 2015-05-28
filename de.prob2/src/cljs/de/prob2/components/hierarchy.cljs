(ns de.prob2.components.hierarchy
  (:require-macros [hiccups.core :as hic])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.dagre-helper :as dh]
            [de.prob2.joint-helper :as jh]))

(defn extract-vertice [e]
  (let [width (if (< 10 (count e)) (* 10 (count e)) 100)]
    {:name e :label e :width width :height 30}))

(defn extract-edge [edge]
  (let [t (:type edge)] (assoc edge :label (name t))))

(defn calculate-dimensions [rel-graph]
  (let [ vertices (mapv (fn [v] (extract-vertice (first v))) rel-graph)
        edges    (mapv extract-edge (mapcat second rel-graph))
        graph    (dh/create-graph vertices edges)]
    (dh/render graph)))

(defn priority-type [{:keys [type]}]
  (if (= type :refines) 0 1))

(defn model-order [element dep-graph]
  (let [next (get dep-graph element [])
        sorted-next (sort-by priority-type next)
        extracted-next (mapv (fn [{:keys [to]}]
                               (model-order to dep-graph)) sorted-next)]
    (cons [element sorted-next] (distinct (apply concat extracted-next)))))

(defn model-order2 [dep-graph queue out used]
  (if (empty? queue) out
      (let [el (first queue)
            next (get dep-graph el [])
            sorted-next (sort-by priority-type next)
            filtered (remove (fn [{:keys [to]}] (used to)) sorted-next)
            names (map :to filtered)]
        (model-order2 dep-graph (concat (rest queue) names) (concat out [[el sorted-next]]) (conj used el)))))

(defn register-click-handle [paper local]
  (.on paper "cell:pointerdown"
       (fn [cell evt _ _]
         (let [attrs (.-attributes (.-model cell))
               type (.-type attrs)]
           (when (= type "basic.Rect")
             (let [next-comp (.-text (.-text (.-attrs attrs)))]
               (swap! local assoc :focused next-comp)))))))

(defn draw-joint-graph [element local-state main-comp dep-graph]
  (let [rel-graph (model-order2 (group-by :from dep-graph) [main-comp] [] #{}) ;(model-order main-comp (group-by :from dep-graph))
        graph (jh/mk-graph)
        paper (jh/mk-paper element graph)
        dagre-graph     (calculate-dimensions rel-graph)
        nodes (jh/get-node-map dagre-graph)
        links (jh/get-links dagre-graph nodes)
        graph2 (-> graph (jh/add-cells (vals nodes))
                   (jh/add-cells links))]
    (set! (.-height (.-style element)) (+ 100 (jh/graph-height graph2)))
    (set! (.-width  (.-style element)) (+ 100 (jh/graph-width graph2)))
    (register-click-handle paper local-state)))

(defn create-component [model local-state]
  (fn [x] (let [dep-graph (:dependency-graph @model)
               component-names (keys (:components @model))
               main-comp (:main-component-name @model)
               _       (swap! local-state assoc :focused main-comp)
               element (.getDOMNode x)]
           (when (and dep-graph main-comp element)
             (draw-joint-graph element local-state main-comp dep-graph)))))

(defn create-canvas [model]
  (fn []
    [:div {:class "col-lg-9"
           :id (str "hierarchy-view" (:main-component-name @model))
           :style {:height 0}}]))

(defn create-hierarchy-view [model local-state]
  (r/create-class
   {:component-did-update (create-component model local-state)
    :component-did-mount  (create-component model local-state) ; for figwheel debugging
    :reagent-render (create-canvas model)}))

(defn extract-formula-list [key component]
  (when-not (empty? (key component))
    [:div (name key)
     [:ul {:class "detail-view-list"}
      (for [f (key component)] [:li {:key (h/fresh-id)} (:formula f)])]]))

(defn context-detail-view [component comp-name]
  [:div (str "CONTEXT " comp-name)
   [extract-formula-list :sets component]
   [extract-formula-list :constants component]])

(defn machine-detail-view [component comp-name]
  [:div (str "MACHINE " comp-name)
   [extract-formula-list :variables component]
   (when-not (empty? (:events component))
     [:div "events"
      [:ul {:class "detail-view-list"}
       (for [e (:events component)] [:li {:key (h/fresh-id)} (:name e)])]])])

(defn create-detail-view [model focused]
  (fn []
    (logp :m @model :f @focused)
    (let [component (get (:components @model) (:focused @focused))
          comp-name (:focused @focused)]
      (if (:variables component)
        [machine-detail-view component comp-name]
        [context-detail-view component comp-name]))))

(defn hierarchy-view [id]
  (fn [id]
    (let [model (rf/subscribe [:model id])
          focus {:focused (:main-component-name @model)}
          focused (r/atom focus)]
      [:div.container
       [:div.row
        [:div.col-lg-3
         [create-detail-view model focused]]
        [create-hierarchy-view model focused]]])))
