#_(defn home-did-mount []
    (.addGraph js/nv (fn []
                       (let [chart (.. js/nv -models lineChart
                                       (margin #js {:left 100})
                                       (useInteractiveGuideline true)
                                       (transitionDuration 350)
                                       (showLegend true)
                                       (showYAxis true)
                                       (showXAxis true))]
                         (.. chart -xAxis
                             (axisLabel "x-axis")
                             (tickFormat (.format js/d3 ",r")))
                         (.. chart -yAxis
                             (axisLabel "y-axis")
                             (tickFormat (.format js/d3 ",r")))

                         (let [my-data [{:x 1 :y 5} {:x 2 :y 3} {:x 3 :y 4} {:x 4 :y 1} {:x 5 :y 2}]]

                           (.. js/d3 (select "#d3-node svg")
                               (datum (clj->js [{:values my-data
                                                 :key "my-red-line"
                                                 :color "red"
                                                 }]))
                               (call chart)))))))


#_(defn dude []
    (fn []
      (let
          [c "<svg xmlns='http://www.w3.org/2000/svg'  style='width:400px; height:400px; background:lightgray;'>
              <foreignObject x='46' y='22' width='200' height='200' requiredExtensions='http://www.w3.org/1999/xhtml'>
                <body xmlns='http://www.w3.org/1999/xhtml'> Suck it up! </body>
              </foreignObject>
           </svg>"

           b (str "data:image/svg+xml;base64," (.btoa js/window c) "")]
        [:img {:src b}])))


#_(defn dude [] (let [s (hic/html [:svg {:xmlns "http://www.w3.org/2000/svg" :id "hx" :style "width:400px; height:400px; background:lightgray;"}
                                   [:circle {:cx 20 :cy 20 :r 20 }]
                                   [:foreignObject {:x 130 :y 123 :width 200 :height 200}
                                    [:body {:style "font-size:18px"  :xmlns "http://www.w3.org/1999/xhtml"}
                                     [:table {:border 1}
                                      [:tr
                                       [:td "1"]
                                       [:td "trololo"]]
                                      [:tr
                                       [:td "2"]
                                       [:td "Muahahaha"]]]]]])
                      b (str "url(data:image/svg+xml;base64," (.btoa js/window s) ")")]
                  (println s)
                  [:div {:style {:height 400 :width 400 :background-image b}}]))


