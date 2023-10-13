(ns mermaid-processor.parse
  (:require [mermaid-processor.parsers.flowchart]
            [mermaid-processor.chart-parser :as parser]))

; Parse our mermaid content
(defn parse-mermaid [content]
  (parser/chart-parser content))