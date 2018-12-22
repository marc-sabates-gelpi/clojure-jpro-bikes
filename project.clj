(defproject jpro-bikes "0.1.0-SNAPSHOT"
  :description "JPro Bikes"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License (GPL) version 3"
            :url "https://www.gnu.org/licenses/gpl.html"}
  :dependencies [[aleph "0.4.6"]
                 [bidi "2.1.4"]
                 [cheshire "5.8.1"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [yada "1.2.16"]]
  :main ^:skip-aot jpro-bikes.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/test.check "0.10.0-alpha3"]]}})
