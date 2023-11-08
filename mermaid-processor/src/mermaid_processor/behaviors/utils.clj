(ns mermaid-processor.behaviors.utils
  "Utility functions for use with behaviours"
  (:require [clojure.string :as str]))

(defn get-field-value 
  "Retrieves the value of a specified field from the context.
    
    ARGUMENTS:
    - context: The context map containing various fields.
    - field-name: The name of the field whose value is to be retrieved.
    
    RETURNS:
    The value of the specified field."
  [context field-name]
  ((context :fields) field-name))

(defn set-field-value 
  "Sets the value of a specified field in the context.
    
    ARGUMENTS:
    - context: The context map.
    - field-name: The name of the field to be set.
    - val: The value to set for the specified field.
    
    RETURNS:
    The updated context with the specified field set to the given value."
  [context field-name val]
  (assoc-in context [:fields field-name] val))

(defn get-last-result 
  "Retrieves the last result value from the context.
    
    ARGUMENTS:
    - context: The context map.
    
    RETURNS:
    The last result value."
  [context]
  ((context :fields) :last-result))

(defn set-last-result 
  "Sets the last result value in the context.
    
    ARGUMENTS:
    - context: The context map.
    - val: The value to set as the last result.
    
    RETURNS:
    The updated context with the last result set to the given value."
  [context val]
  (assoc-in context [:fields :last-result] val))


(defn- safe-equals [lhs rhs] (try (== lhs rhs) (catch ClassCastException _ false)))

(def all-comparators 
  "A list of all the comparators supported pipe seperated"
  ">=|<=|>|<|==|!=|=|larger than|smaller than|greater than|less than|longer than|shorter than|larger than or equals|smaller than or equals|greater than or equals|less than or equals|longer than or equals|shorter than or equals|larger than or equal to|smaller than or equal to|greater than or equal to|less than or equal to|longer than or equal to|shorter than or equal to")

(defn apply-comparator 
  "Compares two values using a specified comparator.
    
    ARGUMENTS:
    - lhs: The left-hand side value.
    - comparator: The comparator to use for comparison. Supports various string representations of comparison operators.
    - rhs: The right-hand side value.
    
    RETURNS:
    The result of the comparison as a boolean.
    
    THROWS:
    - ExceptionInfo if an unknown comparator is provided."
  [lhs comparator rhs]
  (when (nil? lhs) (throw (ex-info "left hand side of comparator is not set" {})))
  (let [rhs (cond
              (keyword? rhs) rhs
              (number? lhs) (if (number? rhs) rhs (Double. rhs))
              :else rhs)]
    (case comparator
      ">=" (>= lhs rhs)
      "larger than or equal to" (>= lhs rhs)
      "greater than or equal to" (>= lhs rhs)
      "longer than or equal to" (>= lhs rhs)
      "larger than or equals" (>= lhs rhs)
      "greater than or equals" (>= lhs rhs)
      "longer than or equals" (>= lhs rhs)
      "<=" (<= lhs rhs)
      "smaller than or equal to" (<= lhs rhs)
      "less than or equal to" (<= lhs rhs)
      "shorter than or equal to" (<= lhs rhs)
      "smaller than or equals" (<= lhs rhs)
      "less than or equals" (<= lhs rhs)
      "shorter than or equals" (<= lhs rhs)
      ">"  (> lhs rhs)
      "larger than" (> lhs rhs)
      "greater than" (> lhs rhs)
      "longer than" (> lhs rhs)
      "<"  (< lhs rhs)
      "smaller than" (< lhs rhs)
      "less than" (< lhs rhs)
      "shorter than" (< lhs rhs)
      "==" (safe-equals lhs rhs)
      "=" (safe-equals lhs rhs)
      "equals" (safe-equals lhs rhs)
      "equal to" (safe-equals lhs rhs)
      "!=" (not (safe-equals lhs rhs))
      "<>" (not (safe-equals lhs rhs)) 
      "not equals" (not (safe-equals lhs rhs))
      "not equal to" (not (safe-equals lhs rhs))
      (throw (ex-info "Unknown comparator" {:comparator comparator})))))
 
(defn kebab-case 
  "Converts a string to kebab-case.
    
    ARGUMENTS:
    - s: The input string.
    
    RETURNS:
    The input string in kebab-case format."
  [s]
  (->> (str/split s #"\s+")
       (map str/lower-case)
       (str/join "-")))

(defn distinct-by 
  "Returns a sequence of distinct elements based on a key function.
    
    ARGUMENTS:
    - key-fn: A function that produces a key for each element.
    - coll: The collection of elements.
    
    RETURNS:
    A sequence of distinct elements based on the produced keys."
  [key-fn coll]
  (let [step (fn step [xs seen]
               (when-let [s (seq xs)]
                 (let [v (key-fn (first s))
                       v-str (if (instance? java.util.regex.Pattern v)
                               (.pattern v)
                               v)]
                   (if (contains? seen v-str)
                     (recur (rest s) seen)
                     (cons (first s) (step (rest s) (conj seen v-str)))))))]
    (step coll #{})))