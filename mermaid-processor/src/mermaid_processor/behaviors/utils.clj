(ns mermaid-processor.behaviors.utils
  (:require [clojure.string :as str]))

(defn field-to-keyword [field-name]
  (cond
    (keyword? field-name) field-name
    (vector? field-name) (field-to-keyword (first field-name))
    :else (keyword (str/lower-case field-name))))

(defn get-field-value [context field-name]
  ((context :fields) (field-to-keyword field-name)))

(defn set-field-value [context field-name val]
  (assoc-in context [:fields (field-to-keyword field-name)] val))

(defn get-last-result [context]
  ((context :fields) :last-result))

(defn set-last-result [context val]
  (assoc-in context [:fields :last-result] val))

(def all-comparators ">=|<=|>|<|==|!=|=|larger than|smaller than|greater than|less than|longer than|shorter than|larger than or equals|smaller than or equals|greater than or equals|less than or equals|longer than or equals|shorter than or equals|larger than or equal to|smaller than or equal to|greater than or equal to|less than or equal to|longer than or equal to|shorter than or equal to")

(defn apply-comparator [lhs comparator rhs]
  (let [rhs (if (number? lhs)
              (try
                (Double. rhs)
                (catch Exception _ rhs))
              rhs)]
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
      "==" (== lhs rhs)
      "=" (== lhs rhs)
      "equals" (== lhs rhs)
      "equal to" (== lhs rhs)
      "!=" (not (== lhs rhs))
      "<>" (not (== lhs rhs))
      "not equals" (not (== lhs rhs))
      "not equal to" (not (== lhs rhs))
      (throw (ex-info "Unknown comparator" {:comparator comparator})))))
 