(ns de.prob2.components.debug-component-structure
  (:require [re-frame.core :as rf]))


(defn title-string [kw]
  (let [s (name kw)
        [[f] l] (split-at 1 s)]
    (apply str (.toUpperCase (str f)) l)))

(defn mk-entry [[k v]]
  [:li
   [:div (title-string k)
    [:ul
     (map (fn [e] [:li (:label e)])
          (remove (fn [e] (= "INITIALISATION" (:label e))) v))
     ]]])

(defn mody-component [[n c]]
  (let [kind (if (contains? c :events) "Machine" "Context")
        c' (into {} (remove (fn [[k v]] (empty? v)) c))
        elems (dissoc c' :invariants :name :variant :axioms)]
       [:div
     [:div (str kind " " n)]
     [:ul
      (map mk-entry elems)]]))

(defn mody [id]
  (let [model (rf/subscribe [:model id])
        formalism (:type @model)
        cs (:components @model)]
    [:div  (map mody-component cs)]))

