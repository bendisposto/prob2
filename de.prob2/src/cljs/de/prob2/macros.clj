(ns de.prob2.macros
  (require [cljs.core.async.macros :refer [go]]))

(defmacro remote-let
  "remote-let fetches a value from the server. It is used like this:

    (remote-let [res (foo x y z)]
        (bar res))

   It assumes that foo is a funtion that is in scop on the server side. 
   The code is executed asynchronously, i.e., foo can be a function that 
   takes some time. The body of the remote-let is executed once res 
   becomes available, but the remote-let itself immediately terminates.

  "
  [[v [command & args]] & body]
  `(go
     (let [c# (cljs.core.async/chan)
           x# (re-frame.core/dispatch [:prob2/call c# identity ~command ~args])
           ~v (cljs.core.async/<! c#)]
       ~@body)))  
