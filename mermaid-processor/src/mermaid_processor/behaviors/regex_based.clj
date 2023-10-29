(ns mermaid-processor.behaviors.regex_based 
  (:require [clojure.string :as str]))

(defn kebab-case [s]
  (->> (str/split s #"\s+")
       (map str/lower-case)
       (str/join "-")))

(defn generate-example-action-map [missing-actions]
  (mapv (fn [action]
          (cond
            (re-find #"(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)" action)
            (let [[_ lhs _ _] (re-find #"(.*?)(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)(.*)[\?]?$" action)]
              {:regex (re-pattern(str #"(?i)" lhs "\\s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\\s*(.*)[\\?]?"))
               :action [:your-library-here (keyword (kebab-case (str/replace lhs #"[^a-zA-Z0-9\s]" ""))) :%1 :%2]})
            :else
            {:regex (re-pattern (str #"(?i)" action "[\\?]?"))
             :action [:your-library-here :your-function-name-here]}))
        missing-actions))

(defn get-implementation-fn [libraries action match]
  (let [actions (libraries (first action))
        action-fn (actions (second action))
        params (mapv (fn [param]
                       (if (and (keyword? param) ; Check if the parameter is a keyword
                                (re-matches #"%[0-9]+" (name param))) ; Check if it starts with :%
                         (let [index (Integer. (subs (name param) 1))] ; Extract the number after the %
                           (match index)) ; Get the captured group based on the number
                         param))
                     (drop 2 action))]
    (apply action-fn params)))

(defn find-matching-action-fn [libraries regex-to-action-map node-text]
  (some (fn [row]
          (when-let [match (re-find (:regex row) node-text)]
            ;; Match will now be the return from re-find. 
            ;; Param 1 should be the action
            ;; The rest of the params params for the action
            (get-implementation-fn libraries (:action row) match)))
        regex-to-action-map))

(defn extract-texts [nodes]
  (distinct
   (concat
    (map :node-text (vals nodes))
    (mapcat (comp (partial map :route-text) :routes) (vals nodes)))))


(defn process-nodes [libraries nodes regex-to-action-map]
  (reduce
   (fn [[cache missing] text]
     (let [matching-action-fn (find-matching-action-fn libraries regex-to-action-map text)]
       (if matching-action-fn
         [(assoc cache text matching-action-fn) missing] ; add to cache if there's a match
         [cache (conj missing text)]))) ; add to missing if there's no match 
   [{} []]
   (extract-texts nodes)))


(defn build [libraries chart regex-to-action-map]
  ;; Build up a cache here (so we don't have to check every regex for every command)
  ;; Also collect any actions that are missing in the chart and throw an exception
  (let [[cache missing] (process-nodes libraries (chart :nodes) regex-to-action-map)]
    (when (seq missing)
      (throw (ex-info "Some nodes or routes did not match any actions" {:missing missing :example-map (generate-example-action-map missing)}))) 
    (fn [command]
      (cache command))))