(ns mermaid-processor.parse-utils)

(defn result-or-exception 
  "Works out if a parse has failed and if so throws an error
   
   ARGUMENTS:
   - parse-result: the result from instaparse.core/parser.
 
   RETURN:
   parse-result unaltered as the abstract syntax tree
   
   EXAMPLE:
   ```
   (result-or-exception (parser input))
   ```
   THROWS:
   ExceptionInfo if the parse-result was a parse error instead of an AST."
  [parse-result]
  (if (contains? parse-result :reason)
    (throw (ex-info "Parsing failed"
                    parse-result))
    parse-result))