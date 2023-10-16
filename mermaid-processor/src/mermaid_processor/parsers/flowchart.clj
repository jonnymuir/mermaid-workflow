(ns mermaid-processor.parsers.flowchart
  (:require [clojure.java.io :as io]
            [mermaid-processor.chart-parser :as parse]
            [instaparse.core :as insta]))

(def grammar
  (slurp (io/resource "mermaid_processor/parsers/flowchart.ebnf")))

(def parser
  (insta/parser grammar :output-format :hiccup))

(defn result-or-exception [result]
  (if (contains? result :reason)
    (throw (ex-info "Parsing failed"
                    result))
    (rest result)))

(defn extract-nodes
  [data]
  (if (not (seq data)) ;; Check if data is non-empty
    []
    (mapcat (fn [item]
              (if (= :node (first item))
                [item]
                (extract-nodes (rest item))))
            data)))


; Define the flowchart method
(defmethod parse/chart-parser :flowchart [_ input]
  (result-or-exception (parser input))
  )