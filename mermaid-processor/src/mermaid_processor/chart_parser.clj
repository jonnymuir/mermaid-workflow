(ns mermaid-processor.chart-parser)

(defmulti parser
  "A multimethod that determines the appropriate parser function based on the type of chart provided.
     
     ARGUMENTS:
     - chart-name: The name or type of the chart (e.g., 'flowchart', 'sequence'). This determines which method implementation will be used.
     - _: A placeholder for the chart content, which is not used in the dispatch function but may be used in method implementations.
  
     RETURN:
     The result of the dispatched method implementation for the given chart type.
  
     USAGE:
     Implementations for specific chart types should be added using `defmethod` with the chart type as the dispatch value."
  (fn [chart-name _] (keyword chart-name)))