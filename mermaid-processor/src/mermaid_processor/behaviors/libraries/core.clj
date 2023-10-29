(ns mermaid-processor.behaviors.libraries.core
  (:require [mermaid-processor.behaviors.utils :as utils]))

;; All actions return a new context and a result as a map
(def actions
  {:set-number
   (fn [field-name value]
     (fn [context]
       (utils/set-field-value context field-name (Double/parseDouble value))))
   :compare
   (fn [field-name comparator value]
     (fn [context]
       {:context context
        :result (let [field-value (utils/get-field-value context field-name)]
          (case comparator
            ">" (> field-value (Double/parseDouble value))))
        }))
   })