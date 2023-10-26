(ns mermaid-processor.behaviors.regex_based
  (:require [clojure.string :as str]))

(defn field-to-keyword [field-name]
  (cond
    (keyword? field-name) field-name
    (vector? field-name) (field-to-keyword (first field-name))
    :else (keyword (str/lower-case field-name))))

(defn get-field-value [context field-name]
  ((context :fields) (field-to-keyword field-name)))

(def actions {
   :set-number (fn [field-name value]
                 (fn [context] 
                   (assoc context 
                          :fields 
                          { (field-to-keyword field-name) (Double/parseDouble value)})))            
})

(def conditions 
  {:comparison (fn [ast] 
                 (fn [context]
                   (let [[field comparator value] ast
                         field-value (get-field-value context field)]
                     (case comparator 
                       ">" (> field-value (Double/parseDouble value))))))})

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

(defn process-nodes [chart regex-to-action-map]
  (reduce
   (fn [[cache missing] node]
     (let [node-text ((second node) :node-text)]
       (if (cache node-text)
         [cache missing] ; already added
         (let [matching-action-fn (find-matching-action-fn regex-to-action-map node-text)]
           (if matching-action-fn
             [(assoc cache node-text matching-action-fn) missing] ; add to cache if there's a match
             [cache (conj missing node)]))))) ; add to missing if there's no match
   [{} []]
   (:nodes chart)))


(defn build-actions [chart regex-to-action-map]
  ;; Build up a cache here (so we don't have to check every regex for every command)
  ;; Also collect any actions that are missing in the chart and throw an exception
  (let [[cache missing] (process-nodes chart regex-to-action-map)]
    (when (seq missing)
      (throw (ex-info "Some node texts did not match any actions" {:missing missing}))) 
    (fn [command]
      (cache command))))
