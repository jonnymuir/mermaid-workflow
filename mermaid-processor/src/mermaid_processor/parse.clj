(ns mermaid-processor.parse
  (:require [mermaid-processor.parsers.flowchart :as flowchart]
            [clojure.string :as str]))

(defn parse-mermaid
  "Parse a mermaid chart
     
ARGUMENTS:
- content: The chart
   
RETURN:
A structure with nodes and routes between nodes
```
{:start-at \"A\"
 :nodes {\"A\" {:node-text \"Desc\" 
                :routes ({:route-destination \"B\" 
                          :route-text \"To\"})}
         \"B\" {:node-text \"Desc2\"
                :routes ()}}
``` 
EXAMPLE:
```
(parse-mermaid \"flowchart TD
                 A[Desc]-->|To|B[Desc2]\")
```    
THROWS:
ExceptionInfo if there was a parse error."

  [content]
  (let [lines (map str/trim (str/split (str/trim content) #"\r?\n"))
        [first-word & _] (str/split (first (remove empty? lines)) #"\s+")]
    ;; We only have one parser at the moment - flowchart, but if we need more
    ;; here is where we route the parse
    (cond 
      (= (str/lower-case first-word) "flowchart")
      (flowchart/parse (str/join "\n" (rest lines)))
      :else
      (throw (ex-info "Unknown chart type"
                      {:type first-word
                       :content content
                       :applies "Chart"})))))
 