(ns mermaid-processor.behaviors.libraries.core
  (:require [mermaid-processor.behaviors.utils :as utils]))

;; All actions return a new context and a result as a map
(def actions
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