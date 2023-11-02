(ns mermaid-processor.behaviors.libraries.core
  "Core library for behaviors, providing basic actions like setting numbers, comparisons, and checking results."
  (:require [mermaid-processor.behaviors.utils :as utils]))

;; All actions return a new context and a result as a map
(def actions
  "A map of core action functions.
   
   :set-nummber [field-name value]
   
   Sets a number to a specified field in the context.
   
   ARGUMENTS:
   - field-name: The name of the field in the context where the number will be set.
   - value: The number value to set.

   :compare [field-name comparator value]
   
   Compares a field value with a given value using a specified comparator.
   
   ARGUMENTS:
   - field-name: The name of the field in the context to compare.
   - comparator: The comparison operator (e.g., '>').
   - value: The value to compare against.

   :last-result-is-true

   Checks if the last result in the context is true.
   
   :last-result-is-not-true

   Checks if the last result in the context is not true
   "
   {:set-number
   (fn [field-name value]
     (fn [context]
       (let [parsed-value (Double/parseDouble value)
             new-context (utils/set-field-value context field-name parsed-value)]
         {:context new-context
          :result parsed-value})))
   :compare
   (fn [field-name comparator value]
     (fn [context]
       {:context context
        :result (let [field-value (utils/get-field-value context field-name)]
                  (case comparator
                    ">" (> field-value (Double/parseDouble value))))}))
   :last-result-is-true
   (fn []
     (fn [context]
       {:context context
        :result (if (utils/get-last-result context) true false)}))
   :last-result-is-not-true
   (fn []
     (fn [context]
       {:context context
        :result (if (utils/get-last-result context) false true)}))})