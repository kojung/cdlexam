(defproject cdlexam "1.0.0-SNAPSHOT"
  :description "Commercial Driver License Exam"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.2"]
                 [ring/ring-devel "0.3.7"]
                 [hiccup "0.3.4"]]
  :dev-dependencies [[appengine-magic "0.4.0"]
                     [swank-clojure "1.2.1"]]
  :jar-exclusions [#"\.svn"])
