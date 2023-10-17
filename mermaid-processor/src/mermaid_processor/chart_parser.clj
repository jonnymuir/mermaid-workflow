(ns mermaid-processor.chart-parser)

;; Define the multimethod chart-parser
;; Its purpose is to take the first word of our content and use that as our actual function name
(defmulti parser 
  (fn [chart-name _] (keyword chart-name)))

(defn result-or-exception 
  "Works out if a parse has failed and if sothrows an error
   
   ARGUMENTS:
   - parse-result: the result from instaparse.core/parser.
 
   RETURN:
   parse-result unaltered as the abstract syntax tree
   
   EXAMPLE:
   (result-or-exception (parser input))
   
   THROWS:
   ExceptionInfo if the parse-result was a parse error instead of an AST. 
   ex-data has the failure in it."
  
  [parse-result]
  (if (contains? parse-result :reason)
    (throw (ex-info "Parsing failed"
                    parse-result))
    (rest parse-result)))
