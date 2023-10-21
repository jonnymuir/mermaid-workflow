(ns mermaid-processor.behaviors.grammar_based
  (:require [instaparse.core :as insta]
            [mermaid-processor.parse-utils :as parse-utils]
            [clojure.string :as str]))

(defn field-to-keyword [field-name]
  (cond
    (keyword? field-name) field-name
    (vector? field-name) (field-to-keyword (first field-name))
    :else (keyword (str/lower-case field-name))))

(defn get-field-value [context field-name]
  ((context :fields) (field-to-keyword field-name)))

(def actions {
   :set-number (fn [ast]
                 (fn [context] 
                   (assoc context 
                          :fields 
                          { (field-to-keyword (first ast)) (Double/parseDouble (second ast))})))            
})

(def conditions 
  {:comparison (fn [ast] 
                 (fn [context]
                   (let [[field comparator value] ast
                         field-value (get-field-value context field)]
                     (case comparator 
                       ">" (> field-value (Double/parseDouble value))))))})

(defn build-actions [action-grammar]
  (let [parser (insta/parser action-grammar :output-format :hiccup)]
    (fn [command]
      (let [ast (parse-utils/result-or-exception (parser command))
            ast (second ast)] 
        ((actions (first ast)) (rest ast))))))

(defn build-conditions [condition-grammar]
  (let [parser (insta/parser condition-grammar :output-format :hiccup)]
    (fn [condition]
      (let [ast (parse-utils/result-or-exception (parser condition))
            ast (second ast)]
        (prn ast)
        ((conditions (first ast)) (rest ast))))))
