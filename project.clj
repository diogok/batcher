(defproject batcher "0.0.2"
  :description "Buffer and Batch operations, by time or count."
  :url "http://github.com/diogok/batcher"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.2.371"]]
  :repositories [["clojars" {:sign-releases false}]]
  :profiles {:dev {:dependencies [[midje "1.8.2"]]
                   :plugins [[lein-midje "3.2"]]}})
