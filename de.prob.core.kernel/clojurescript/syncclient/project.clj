(defproject syncclient "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2234"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "syncclient"
              :source-paths ["src"]
              :compiler {
                :output-dir "../../src/main/resources/gui/js/syncclient/out"
                :output-to "../../src/main/resources/gui/js/syncclient/syncclient.js"
                :optimizations :none
                :source-map "../../src/main/resources/gui/js/syncclient/syncclient.js.map"}}]})
