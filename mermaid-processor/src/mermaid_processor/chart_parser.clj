(ns mermaid-processor.chart-parser
  (:require [clojure.string]))

; Define the multimethod chart-parser
; Its purpose is to take the first word of our content and use that as our actual function name
(defmulti chart-parser (fn [content] (keyword (first (clojure.string/split content #" ")))))
