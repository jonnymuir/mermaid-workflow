(ns mermaid-processor.parse
  (:require [mermaid-processor.parsers.flowchart]
            [mermaid-processor.chart-parser :as parser]
            [clojure.string :as str]))

; Parse our mermaid content
(defn parse-mermaid [content]
  (let [lines (map str/trim (str/split (str/trim content) #"\r?\n"))
        [first-word & _] (str/split (first (remove empty? lines)) #"\s+")]
    (parser/chart-parser first-word (str/join "\n" (rest lines)))))