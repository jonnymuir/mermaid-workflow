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

(defn process-node-or-route [node-or-route]
  ;; if a node - return node-text.
  ;; if a route - return route-text.
  ;; also return the routes.
  (if (vector? node-or-route)
    {:text ((second node-or-route) :node-text) :routes ((second node-or-route) :routes)}
    {:text (node-or-route :route-destination) :routes nil}))

(defn process-nodes-or-routes [nodes-or-routes regex-to-action-map]
  (reduce
   (fn [[cache missing] node-or-route]
     (let [{text :text, routes :routes} (process-node-or-route node-or-route) 
           new-cache-missing
           (if (cache text)
             [cache missing]
             (let [matching-action-fn (find-matching-action-fn regex-to-action-map text)]
               (if matching-action-fn
                 [(assoc cache text matching-action-fn) missing] ; add to cache if there's a match
                 [cache (conj missing node-or-route)])))] ; add to missing if there's no match 
       ;; If we have routes then go and process these and add them to our cache / missing
       ;; otherwise return what we have
       (if routes 
         (vec (concat new-cache-missing (process-nodes-or-routes routes regex-to-action-map)))
         new-cache-missing)))       
   [{} []]
   nodes-or-routes))


(defn build [chart regex-to-action-map]
  ;; Build up a cache here (so we don't have to check every regex for every command)
  ;; Also collect any actions that are missing in the chart and throw an exception
  (let [[cache missing] (process-nodes-or-routes (:nodes chart) regex-to-action-map)]
    (when (seq missing)
      (throw (ex-info "Some nodes or routes did not match any actions" {:missing missing}))) 
    (fn [command]
      (cache command))))