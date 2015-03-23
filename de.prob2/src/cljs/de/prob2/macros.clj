(ns de.prob2.macros)

(defmacro remote-let [[v [m & args]] & body]
      `(~'go (let [c# (async/chan)
      	          x# (rf/dispatch [:prob2/call c# ~(str m) ~@args])
      	          ~v (async/<! c#)]
             ~@body)))

