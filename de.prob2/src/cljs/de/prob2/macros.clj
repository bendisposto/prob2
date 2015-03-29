(ns de.prob2.macros
  (:require [cljs.core.async.macros]))

(defmacro remote-let
  "remote-let calls a Groovy function on the server and fetches the
  result value. It is used like this:

  (remote-let [res (foo x y z)]
  (bar res))

  It assumes that foo is a function that has been registered in the
  UIFunctionregistry on the server side.
  The code is executed asynchronously, i.e., foo can be a function that
  takes some time. The body of the remote-let is executed once res
  becomes available, but the remote-let itself immediately terminates.

  "
  [[v [command & args]] & body]
  `(cljs.core.async.macros/go
     (let [c# (cljs.core.async/chan)
           x# (re-frame.core/dispatch [:prob2/call c# identity ~(str command) ~@args])
           ~v (cljs.core.async/<! c#)]
       ~@body)))
