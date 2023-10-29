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
  {:context (assoc-in context [:fields (field-to-keyword field-name)] val)
   :result val})

 