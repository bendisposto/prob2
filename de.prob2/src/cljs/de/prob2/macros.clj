(ns de.prob2.macros)

(defmacro remote-let [[v [m & args]] & body]
  `(cljs.core.async.macro/go
     (let [c# (cljs.core.async/chan)
           x# (re-frame.core/dispatch [:prob2/call c# ~(str m) ~@args])
           ~v (cljs.core.async/<! c#)]
       ~@body)))
