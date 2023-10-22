(ns mermaid-processor.validate)

(defn validate-chart-behaviors
  "
  Validates that all nodes and routes in the chart have corresponding actions and conditions in the provided behaviors.

  ARGUMENTS:
  - behaviors: A map defining actions and conditions for nodes and routes.
  - chart: The chart structure containing nodes and routes.

  RETURN:
  A report or status indicating the validation results, highlighting any inconsistencies found.

  EXAMPLE:
  (validate-chart-behaviors (build/behaviors ...) (parse/chart ...)


  THROWS:"
  [behaviors chart]
    {:missing-actions (distinct (reduce
                       (fn [missing node]
                         (if (nil? ((behaviors :actions) ((second node) :node-text)))
                           (conj missing {:action ((second node) :node-text)})
                           missing))
                       []
                       (chart :nodes)))
     :missing-conditions (distinct (reduce
                          (fn [missing node]
                            (reduce
                             (fn [missing route]
                               (let [condition (route :route-text)]
                                 (if (nil? ((behaviors :conditions) condition))
                                   (conj missing {:condition condition})
                                   missing)))
                             missing
                             ((second node) :routes)))
                          []
                          (chart :nodes)))})
