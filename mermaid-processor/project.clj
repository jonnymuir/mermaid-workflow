(defproject mermaid-processor "0.1.0-SNAPSHOT"
  :description "A project which creates a runable version of a mermaid diagram"
  :url "https://github.com/jonnymoo/mermaid-workflow/mermaid-processor"
  :license {:name "MIT License"
            :url "https://github.com/jonnymoo/mermaid-workflow/blob/main/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [instaparse "1.4.12"]]
  :main ^:skip-aot mermaid-processor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})