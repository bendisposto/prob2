(ns prob-ui.test-data)

(defn uuid [] (-> (java.util.UUID/randomUUID) str keyword))

(def history-tt (let [u (uuid)] [[[:current-animation :uuid] u]
                                 [[:current-animation :component] "foo.mch"]
                                 [[u :1 :name] "a"]
                                 [[u :1 :type] "past"]
                                 [[u :2 :name] "b"]
                                 [[u :2 :type] "past"]
                                 [[u :3 :name] "c"]
                                 [[u :3 :type] "current"]
                                 [[u :4 :name] "d"]
                                 [[u :4 :type] "future"]]))


(def groovy-tt [
                [["groovy" 0 "in"] "12 + 4"]
                [["groovy" 0 "status"] "ok"]
                [["groovy" 0 "result"] "16"]
                ])
