(defproject com.jonnymuir/mermaid-processor "0.1.2-SNAPSHOT"
  :description "A project which creates a runable version of a mermaid diagram"
  :url "https://github.com/jonnymoo/mermaid-workflow/mermaid-processor"
  :scm "https://github.com/jonnymoo/mermaid-workflow/tree/main/mermaid-processor/src"
  :license {:name "MIT License"
            :url "https://github.com/jonnymoo/mermaid-workflow/blob/main/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [instaparse "1.4.12"]
                 [org.clojure/data.zip "1.0.0"]
                 [org.clojure/data.xml "0.2.0-alpha6"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[lein-codox "0.10.8"]]
  :codox {:output-path "docs"
          :source-uri "https://github.com/jonnymoo/mermaid-workflow/blob/main/mermaid-processor/{filepath}#L{line}"}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/LEIN_USERNAME
                                    :password :env/LEIN_PASSWORD }]])

