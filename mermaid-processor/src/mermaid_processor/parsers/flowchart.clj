(ns mermaid-processor.parsers.flowchart
  (:require [clojure.string]
            [mermaid-processor.chart-parser :as parse]))

; Define the flowchart method
(defmethod parse/chart-parser :flowchart [content]
  '())