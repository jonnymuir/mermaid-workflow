(ns mermaid-processor.parse
  (:require [mermaid-processor.parsers.flowchart]
            [mermaid-processor.chart-parser :as chart-parser]
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
    (chart-parser/parser first-word (str/join "\n" (rest lines)))))