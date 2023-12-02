(ns mermaid-processor.behaviors.regex_based 
  (:require [clojure.string :as str]
            [mermaid-processor.behaviors.utils :as utils]))

(defn- generate-example-action-map [missing-actions]
  (->> missing-actions
       (map (fn [action]
              (cond
                (re-find (re-pattern (str "(?i)(" utils/all-comparators ")")) action)
                (let [[_ lhs _ _] (re-find (re-pattern (str "(?i)^\\s*(.*?)(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*\\s*$")) action)]
                  {:regex (re-pattern (str "(?i)^\\s*" lhs "\\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*\\s*$"))
                   :action [:your-library-here (keyword (utils/kebab-case (str/replace lhs #"[^a-zA-Z0-9\s]" ""))) :%1 :%2]})
                (re-find #"(?i)^\s*(yes|true|is true)[\?]?\s*$" action)
                {:regex #"(?i)^\s*(Yes|True|Is True)[\?]?\s*$"
                 :action [:core :last-result-is-true]}
                (re-find #"(?i)^\s*(no|false|is false|is not true)[\?]?\s*$" action)
                {:regex #"(?i)^\s*(No|False|Is False|Is Not True)[\?]?\s*$"
                 :action [:core :last-result-is-not-true]}
                :else
                {:regex (re-pattern (str "(?i)^\\s*" (str/replace action "?" "") "[\\?]?\\s*$"))
                 :action [:your-library-here :your-function-name-here]})))
       (utils/distinct-by :regex)
       (into [])))


(defn- execute-chained-actions [action-fns initial-context]
  (let [initial-accumulator {:context initial-context
                             :result nil
                             :audit []}]
    (reduce (fn [accumulator action-fn]
              (let [context (accumulator :context)
                    action-result (action-fn context)
                    new-context (action-result :context)
                    new-audit (conj (accumulator :audit) (get action-result :audit nil))
                    new-result (action-result :result)]
                {:context new-context
                 :audit new-audit
                 :result new-result}))
            initial-accumulator
            action-fns)))


(defn- get-single-implementation-fn [libraries action match]
  (let [actions (libraries (first action))
        action-fn (actions (second action))
        params (mapv
                (fn [param]
                  (if (and (keyword? param) ; Check if the parameter is a keyword
                           (re-matches #"%[0-9]+" (name param))) ; Check if it starts with :%
                    (let [index (Integer. (subs (name param) 1))] ; Extract the number after the %
                      (match index)) ; Get the captured group based on the number
                    param))
                (drop 2 action))]
    (when (not action-fn)
      (throw (ex-info "Unknown action"
                      {:action action
                       :match match})))
    (try
      (apply action-fn params)
      (catch Exception e
        (throw (ex-info "An error occurred while applying the action function."
                        {:action action
                         :match match
                         :original-message (.getMessage e)}
                        e))))))

(defn- single-action? [action-or-action-list]
  (and (vector? action-or-action-list)
       (not (vector? (first action-or-action-list)))))

(defn- get-implementation-fn [libraries action-or-action-list match]
  (let [actions (if (single-action? action-or-action-list)
                  [action-or-action-list]
                  action-or-action-list)
        action-fns (map #(get-single-implementation-fn libraries % match) actions)]
    (fn [context]
      (execute-chained-actions action-fns context))))

(defn- find-matching-action-fn [libraries regex-to-action-map node-text]
  (some
   (fn [row]
     (try
       (when-let [match (re-find (:regex row) node-text)]
         (get-implementation-fn libraries (:action row) match))
       (catch Exception e
         (throw (ex-info "Error applying regular expression"
                         {:row row
                          :node-text node-text
                          :original-exception e})))))
   regex-to-action-map))

(defn- extract-texts [nodes]
  (distinct
   (concat
    (remove str/blank? (map :node-text (vals nodes)))
    (remove str/blank? (mapcat (comp (partial map :route-text) :routes) (vals nodes))))))


(defn- process-nodes [libraries nodes regex-to-action-map]
  (reduce
   (fn [[cache missing] text]
     (let [matching-action-fn (find-matching-action-fn libraries regex-to-action-map text)]
       (if matching-action-fn
         [(assoc cache text matching-action-fn) missing] ; add to cache if there's a match
         [cache (conj missing text)]))) ; add to missing if there's no match 
   [{} []]
   (extract-texts nodes)))


(defn build 
  "Constructs a behavior function for a given chart using specified libraries and a regex-to-action map.
  
  ARGUMENTS:
  - libraries: A collection of libraries containing predefined actions and conditions.
  - chart: The mermaid chart content, parsed into a map with nodes, routes, etc.
  - regex-to-action-map: A map where keys are regex patterns and values are corresponding actions.
  
  The function processes the nodes of the chart, attempting to match each node's text with a regex pattern from the regex-to-action map. If all nodes are matched, it returns a behavior function that, when invoked with a command, will execute the corresponding action or condition from the cache. If any nodes are not matched, it throws an exception detailing the missing matches.
  
  RETURNS:
  A behavior function that takes a command and executes the corresponding action or condition from the cache.
  
  THROWS:
  - ExceptionInfo if there are nodes or routes in the chart that do not match any actions in the regex-to-action map.
  
  EXAMPLE:
  ```
  (build libraries chart regex-to-action-map)
  ```
   
  NOTE:
  The returned behavior function is essentially a cached lookup for actions and conditions based on the command it receives.
  This is typically used to pass into the process/process-chart function."
  [libraries chart regex-to-action-map]
  (let [[cache missing] (process-nodes libraries (chart :nodes) regex-to-action-map)]
    (when (seq missing)
      (throw (ex-info "Some nodes or routes did not match any actions" {:missing missing :example-map (generate-example-action-map missing)}))) 
    (fn [command]
      (cache command))))