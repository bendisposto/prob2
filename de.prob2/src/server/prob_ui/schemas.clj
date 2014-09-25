(ns prob-ui.schemas
  (:use [schema.core :exclude [fn defn defmethod letfn defrecord]]))

(def State
 {Keyword Any})

(def Transaction
  [(one [Keyword] "path") (one Any "value")])

(def Transactions
  [(one Transaction "first") Transaction])


