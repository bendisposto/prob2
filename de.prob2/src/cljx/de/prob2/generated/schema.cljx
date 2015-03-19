(ns de.prob2.generated.schema
  (:require [schema.core :as s]))


(def Formula-Spec {:formula s/Str :formula-id s/Str (s/optional-key :label) s/Str})

(def Theorem?-Formula-Spec
  (merge {:theorem? s/Bool} Formula-Spec))

(def EventB-Context-Spec
  {:name s/Str
   :sets [Formula-Spec]
   :constants [Formula-Spec]
   :axioms [Theorem?-Formula-Spec]})

(def B-Machine-Spec
  {:name s/Str
   :variables [Formula-Spec]
   :invariants [Theorem?-Formula-Spec]})
 
(def B-Event-Spec
  {:name s/Str
   :label s/Str
   :parameters [s/Str]
   :actions [s/Str]})

(def Event-Spec
  (merge B-Event-Spec
         {:witnesses [s/Str]
          :refines [s/Str] ;; multiple events = merge
          :kind (s/enum :ordinary :anticipated :convergent)
          :guards [Theorem?-Formula-Spec]}))

(def Operation-Spec
  (merge B-Event-Spec
         {:guards [Formula-Spec]
          :return-values [s/Str]}))

(def EventB-Machine-Spec
  (merge B-Machine-Spec
         {:variant [Formula-Spec]
          :events [Event-Spec]}))

(def ClassicalB-Machine-Spec
  (merge B-Machine-Spec
         {:parameters [Formula-Spec]
          :sets [Formula-Spec]
          :constraints [Formula-Spec]
          :constants [Formula-Spec]
          :properties [Formula-Spec]
          :events [Operation-Spec]}))

(def Component-Spec
  (s/either EventB-Machine-Spec
            EventB-Context-Spec
            ClassicalB-Machine-Spec))

(def Graph-Spec [{:from s/Str :to s/Str :type (s/enum :sees :uses :refines :includes :imports :extends)}])

(def Model-Spec {:dir s/Str
                 :main-component-name s/Str
                 :filename s/Str
                 :type s/Keyword
                 :dependency-graph Graph-Spec
                 :components {s/Str Component-Spec}
                 })

(def Animatorstate-Spec {:model s/Str :state s/Str})

(def Transition-Spec
  {:id s/Str
   :name s/Str
   :parameters [s/Str]
   :return-values [s/Str]
   :src Animatorstate-Spec
   :dst Animatorstate-Spec})

(def Trace-Spec
  {:trace-id s/Uuid
   :transitions [Transition-Spec]
   :current-index s/Int
   :out-transitions [Transition-Spec]
   :current-state Animatorstate-Spec
   :current-transition (s/maybe Transition-Spec)
   :back? s/Bool
   :forward? s/Bool
   :model s/Str
   })

(def State-Spec
  {:values {s/Str  s/Int}
   :initialized? s/Bool
   :inv-ok? s/Bool
   :timeout? s/Bool
   :max-transitions-reached? s/Bool
   :id Animatorstate-Spec
   :state-errors [{:event s/Str :short-desc s/Str :long-desc s/Str}]
   :events-with-timeout [s/Str]})


(def Transmitted-State-Spec
  {:traces {s/Uuid Trace-Spec}
   :models {s/Str Model-Spec}
   :states {Animatorstate-Spec State-Spec}
   :results {s/Int s/Str}})

(def UI-State
  (merge Transmitted-State-Spec
         {:connected s/Bool}))

