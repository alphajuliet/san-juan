(defproject san-juan "0.1.0-SNAPSHOT"
  :description "Implement San Juan game engine."
  :url "https://alphajuliet.com/ns/san-juan/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [random-seed "1.0.0"]
                 [org.clojure/math.combinatorics "0.1.6"]]
  :repl-options {:init-ns san-juan.core})
