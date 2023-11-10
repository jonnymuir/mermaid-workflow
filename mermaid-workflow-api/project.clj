(defproject com.jonnymuir/mermaid-workflow-api "0.1.0-SNAPSHOT"
  :description "A Clojure project to provide a web API for mermaid-workflow processing"
  :url "https://github.com/jonnymoo/mermaid-workflow/mermaid-workflow-api"
  :scm "https://github.com/jonnymoo/mermaid-workflow/tree/main/mermaid-workflow-api/src"
  :license {:name "MIT License"
            :url "https://github.com/jonnymoo/mermaid-workflow/blob/main/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/jsonista "0.2.6"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.7.0-alpha7"]
                 [metosin/ring-swagger-ui "5.9.0"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.0"]
                 [com.jonnymuir/mermaid-processor "0.1.7-SNAPSHOT"]]
  
  :repl-options {:init-ns mermaid-workflow-api.server}
  :main mermaid-workflow-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[lein-codox "0.10.8"]]
  :codox {:output-path "docs"
          :source-uri "https://github.com/jonnymoo/mermaid-workflow/blob/main/mermaid-workflow-api/{filepath}#L{line}"})