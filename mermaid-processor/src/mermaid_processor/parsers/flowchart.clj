(ns mermaid-processor.parsers.flowchart
  (:require [clojure.java.io :as io]
            [mermaid-processor.chart-parser :as chart-parser]
            [instaparse.core :as insta]))

(def grammar
  (slurp (io/resource "mermaid_processor/parsers/flowchart.ebnf")))

(def parser
  (insta/parser grammar :output-format :hiccup))

(defn transform [ast]
  (apply hash-map (flatten ast)))

;; Parse a mermaid flow chart
(defmethod chart-parser/parser :flowchart 
  [_ input]
  (transform (chart-parser/result-or-exception (parser input))))