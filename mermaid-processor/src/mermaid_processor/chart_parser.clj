(ns mermaid-processor.chart-parser)

; Define the multimethod chart-parser
; Its purpose is to take the first word of our content and use that as our actual function name
(defmulti chart-parser (fn [chart-name _] (keyword chart-name)))