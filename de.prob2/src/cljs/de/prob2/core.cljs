(ns de.prob2.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [de.prob2.client :as client]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.schema :as schema]
            [schema.core :as s]))

;; -------------------------
;; Views
                                        ;(sente/set-logging-level! :trace)

(declare home-page disconnected-page)

(def *validation* false)

(def state (atom {:traces {} :models {} :states {} :results {} :connected false}
                 :validator
                 (fn [new-state]
                   (logp :new-state new-state)
                   (if  *validation*
                     (try
                       (s/validate schema/UI-State new-state)
                       (catch js/Object e
                         (.log js/console e)
                         (logp new-state)))
                     new-state))))


(def id-store (clojure.core/atom 0))
(defn fresh-id []
  (let [x @id-store]
    (swap! id-store inc) x))



(defn connect []
  (client/chsk-send! [:chsk/encoding nil])
  (swap! state assoc :connected true)
  (session/put! :current-page #'home-page))

(defn disconnect []
  (session/put! :current-page #'disconnected-page)
  (reset! state {:traces {} :connected false}))

(add-watch
 client/chsk-state
 :chsk-observer
 (fn [_ _ {oo :open?} {no :open?}]
   (cond (and no (not oo)) (connect)
         (and oo (not no)) (disconnect)
         :otherwise nil)))


(defn fix-names [name]
  (get {"$initialise_machine" "INITIALISATION"
        "$setup_constants" "SETUP CONSTANTS"} name name))

(defn dissoc-in
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (assoc m k newmap))
      m)
    (dissoc m k)))


(defmethod client/handle :de.prob2.kernel/ui-state [[_ msgs]]
  (doseq [[uuid trace] (:traces msgs)]
    (swap! state assoc-in [:traces uuid] trace )))

(defmethod client/handle :de.prob2.kernel/trace-removed [[_ msgs]]
  (doseq [uuid msgs]
    (swap! state dissoc-in [:traces uuid])))





(defn state-row [name current-value previous-value]
  [:tr [:td name] [:td current-value] [:td previous-value]])

(defn state-view []
  (let [{:keys [trace-id current previous transition]} (:focused @state)
        names (map first (:values current))
        cvals (map second (:values current))
        pvals (map second (:values previous))]
    [:div (str  trace-id)]
    (into [:table {:class "table"}] (map (fn [n c p] [state-row n c p]) names cvals pvals))
    ))

(defn pp-transition [{:keys [name parameters return-values]}]
  (let [ppp (if (seq parameters) (str "(" (clojure.string/join "," parameters) ")") "")
        pprv (if (seq return-values) (str (clojure.string/join "," return-values) \u21DC " ")  "")
        fname (fix-names name)] (str pprv fname ppp)))

(defn- mk-history-item [trace-id current {:keys [index] :as item}]
  ^{:key (str "h" index)}
  [:li
   [:a  {:class (str "history-item" (cond (= current index) " current "
                                          (< current index) " future "
                                          :default ""))
         :on-click (fn [_] (client/send! :history/goto {:trace-id trace-id :index index}))}
    (pp-transition item)]])

(defn history-view []
  (let [sort-order (atom identity)]
    (fn []
      (let [id (session/get :focused-uuid)
            t (get-in @state [:traces id])
            h (cons {:name "-- uninitialized --" :return-values [] :parameters [] :id -1 :index -1} (map-indexed (fn [index element] (assoc element :index index)) (:history t)))]
        [:div {:class "history-view"}
         [:div {:class "glyphicon glyphicon-sort pull-right"
                :id "sort-button"
                :on-click (fn [_] (swap! sort-order
                                        (fn [f] (get {identity reverse} f identity))))}]
         [:ul {:class "history-list"}
          (map (partial mk-history-item (:trace-id t) (:current-index t)) (@sort-order h))]
         ]))))


(defn surrounding [v c n]
  (let [[a v'] (split-at (- c n) v)
        [b c] (split-at (inc (* n 2)) v')]
    [(count a) b (count c)]))

(defn p-fix [c]
  (when (< 0 c) [{:active "" :pp (str "..(" c ")..")}]))

(defn trace-excerpt [{:keys [history current-index] :as t}]
  (let [[pre hv post] (surrounding (into [] (map-indexed vector history)) current-index 2)]
    ^{:key (fresh-id)} (concat
                        (p-fix pre)
                        (map (fn [[i e]]
                               (let [a? (= i current-index)]
                                 (assoc e
                                        :active (if a? "active" "")
                                        :pp (if a? (pp-transition e) (fix-names (:name e)))))) hv)
                        (p-fix post))))

(defn pp-trace-excerpt [t]
  ^{:key (fresh-id)} [:span {:class (str "pp-trace-item " (:active t))} (:pp t)])

(defn mk-span []
  ^{:key (fresh-id)} [:span ", "])

(defn mk-trace-item [{:keys [current-index trace-id history] :as p}]
  ^{:key trace-id} [:li {:class "animator"}
                    [:a {:href (str "#/trace/" trace-id)}
                     (let [t (trace-excerpt p)] (if (seq t) (interpose [mk-span]
                                                                       (map pp-trace-excerpt t)) "empty trace"))]])

(defn mk-animator-sublist [[id elems]]
  (let [trace-ids (map :trace-id elems)
        {:keys [animator-id main-component-name file]} (:model (first elems))]
    ^{:key (fresh-id)}
    [:li {:class "animator-sublist"}
     [:div {:class "model"}
      [:span {:class "glyphicon glyphicon-remove" :id "animator-remove-btn" :on-click (fn [_] (client/send! :prob2/kill! {:animator-id id :trace-ids trace-ids}))}]
      [:span (str main-component-name " (" file ")")]]
     [:ul {:class "animator-list"}
      (if (seq elems)
        (map mk-trace-item elems)
        [:div]
        )]]))

(defn trace-selection-view []
  (let [raw-traces (:traces @state)
        grouped (group-by :animator-id (vals raw-traces))]
    [:div {:class "trace-selector"}
     [:ul {:class "trace-list"}
      (if (seq grouped)
        (map mk-animator-sublist grouped)
        [:div])]]))

(defn status-view []
  (let [raw-traces (:traces @state)
        grouped (group-by :animator-id (vals raw-traces))]
    [:div "Running animators:" (count grouped)]))

(defn home-page []
  [trace-selection-view])

(defn about-page []
  [:div [:h2 "About de.prob2"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn animation-view []
  [:div {:id "h1"} [history-view]])

(defn disconnected-page []
  [:div {:class "alert alert-danger"}
   [:h4 "Disconnected"]
   [:p "The client has lost the connection to the server. You can try reloading this page, but maybe you need to check your connection or restart the server."]])
