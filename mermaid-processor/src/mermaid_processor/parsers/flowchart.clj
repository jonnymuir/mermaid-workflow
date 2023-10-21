(ns mermaid-processor.parsers.flowchart
  (:require [clojure.java.io :as io]
            [mermaid-processor.chart-parser :as chart-parser]
            [instaparse.core :as insta]
            [mermaid-processor.parse-utils :as parse-utils]))

(def grammar
  (slurp (io/resource "mermaid_processor/parsers/flowchart.ebnf")))

(def parser
  (insta/parser grammar :output-format :hiccup))

(defn get-by-keyword [vector keyword]
  (rest (first (filter #(= (first %) keyword) vector))))

(defn get-by-keyword-single [vector keyword] 
  (first (get-by-keyword vector keyword)))

(defn process-node [node-map node]
  ;; If the node does exist - create it, otherwise if text is passed update it  
  (let [node-id (get-by-keyword-single node :node-id)
        node-text (get-by-keyword-single node :node-text)]
    (cond
      (not (contains? node-map node-id)) 
         (assoc node-map node-id 
                {:node-text (or node-text node-id) 
                 :routes []})
      node-text 
          (assoc-in node-map [node-id :node-text] node-text)
      :else node-map)))

(defn add-route [node-map source-node-id destination-node-id route-text ]
  (update node-map
          source-node-id
          (fn [node]
            (update node :routes
                    (fn [routes]
                      (conj routes {:route-destination destination-node-id
                                    :route-text route-text}))))))

(defn process-route [node-map route-or-node]
  ;; Processes a route and returns [last-id updated-map]
  ;; if we are passed a node - then add to the map and return it as the last-id
  ;; otherwise recurse
  (let [node-id (get-by-keyword-single route-or-node :node-id)]
    (if node-id 
      (let [updated-map (process-node node-map route-or-node)]
               [updated-map node-id])
      (let [[source-updated-map source-node-id] (process-route node-map (rest (get-by-keyword-single route-or-node :route-source)))
            destination-node (get-by-keyword route-or-node :node)
            destination-node-id (get-by-keyword-single destination-node :node-id)
            updated-map (process-node source-updated-map destination-node)]
        [(add-route updated-map source-node-id destination-node-id (get-by-keyword-single route-or-node :route-text)) destination-node-id]))))

(defn process-ast [node-map item]
  (cond
    (= (first item) :node) (process-node node-map (rest item))
    (= (first item) :route) (first (process-route node-map (rest item)))
    :else node-map)) 


(defn find-first-node-id
  [structure]
  (when-let [s (seq structure)] 
    (cond
      (vector? (first s)) (recur (first s))
      (= (first s) :node-id) (second s)
      :else (recur (rest s)))))

(defn transform [ast]
  (reduce process-ast {} ast))

;; Parse a mermaid flow chart
(defmethod chart-parser/parser :flowchart
  [_ input]
  (let [ast (rest (parse-utils/result-or-exception (parser input)))]
  {:start-at (find-first-node-id ast) :nodes (transform ast)}))