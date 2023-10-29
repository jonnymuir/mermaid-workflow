(ns mermaid-processor.behaviors.regex_based
  (:require [clojure.string :as str]))

(defn field-to-keyword [field-name]
  (cond
    (keyword? field-name) field-name
    (vector? field-name) (field-to-keyword (first field-name))
    :else (keyword (str/lower-case field-name))))

(defn get-field-value [context field-name]
  ((context :fields) (field-to-keyword field-name)))

;; All actions return a new context and a result as a map
(def actions
  {:set-number
   (fn [field-name value]
     (fn [context]
       (let [double-value (Double/parseDouble value)]
         {:context (assoc-in context [:fields (field-to-keyword field-name)] double-value)
          :result double-value})))
   :compare
   (fn [field-name comparator value]
     (fn [context]
       {:context context
        :result (let [field-value (get-field-value context field-name)]
          (case comparator
            ">" (> field-value (Double/parseDouble value))))
        }))
   })

(defn get-implementation-fn [action match]
  (let [action-fn (actions (first action))
        params (mapv (fn [param]
                       (if (and (keyword? param) ; Check if the parameter is a keyword
                                (re-matches #"%[0-9]+" (name param))) ; Check if it starts with :%
                         (let [index (Integer. (subs (name param) 1))] ; Extract the number after the %
                           (match index)) ; Get the captured group based on the number
                         param))
                     (rest action))]
    (apply action-fn params)))

(defn find-matching-action-fn [regex-to-action-map node-text]
  (some (fn [row]
          (when-let [match (re-find (:regex row) node-text)]
            ;; Match will now be the return from re-find. 
            ;; Param 1 should be the action
            ;; The rest of the params params for the action
            (get-implementation-fn (:action row) match)))
        regex-to-action-map))

(defn extract-texts [nodes]
  (distinct
   (concat
    (map :node-text (vals nodes))
    (mapcat (comp (partial map :route-text) :routes) (vals nodes)))))


(defn process-nodes [nodes regex-to-action-map]
  (reduce
   (fn [[cache missing] text]
     (let [matching-action-fn (find-matching-action-fn regex-to-action-map text)]
       (if matching-action-fn
         [(assoc cache text matching-action-fn) missing] ; add to cache if there's a match
         [cache (conj missing text)]))) ; add to missing if there's no match 
   [{} []]
   (extract-texts nodes)))


(defn build [chart regex-to-action-map]
  ;; Build up a cache here (so we don't have to check every regex for every command)
  ;; Also collect any actions that are missing in the chart and throw an exception
  (let [[cache missing] (process-nodes (chart :nodes) regex-to-action-map)]
    (when (seq missing)
      (throw (ex-info "Some nodes or routes did not match any actions" {:missing missing}))) 
    (fn [command]
      (cache command))))