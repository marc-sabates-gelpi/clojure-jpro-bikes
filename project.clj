(defproject jpro-bikes "0.1.0-SNAPSHOT"
  :description "JPro Bikes"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License (GPL) version 3"
            :url "https://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [bidi "2.1.4"]
                 [yada "1.2.16"]
                 [cheshire "5.8.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :main ^:skip-aot jpro-bikes.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
