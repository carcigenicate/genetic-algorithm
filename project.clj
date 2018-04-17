(defproject genetic-algorithm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [helpers "1"]
                 [seesaw "1.4.5"]
                 [criterium "0.4.4"]]
  :main ^:skip-aot genetic-algorithm.text-ui.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
