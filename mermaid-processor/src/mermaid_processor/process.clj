(ns mermaid-processor.process)

(defn get-current-node [context behaviors chart]
  ((chart :nodes) ((behaviors :get-current-node-id) context chart)))

(defn get-current-node-text [context behaviors chart]
  (let [current-node (get-current-node context behaviors chart)]
    (current-node :node-text)))

(defn process-routes [context behaviors routes ]
  (some (fn [route]
          (if-let [route-text (:route-text route)]
            (if-let [condition-fn ((:conditions behaviors) route-text)]
              (when (condition-fn context)
                route)
              (throw (ex-info (str "Condition not found: " route-text)
                              {:route-text route-text :context context})))
            route))
        routes))

(defn run-action [context behaviors action]
  (let [action-fn ((behaviors :actions) action)]
    (if action-fn
      (action-fn context)
      (throw (ex-info (str "Action not found: " action)
                      {:action action :context context})))))

(defn process-chart [context behaviors chart]
  (let [new-context (run-action context
                                behaviors
                                (get-current-node-text context behaviors chart))
        route (process-routes new-context
                              behaviors
                              ((get-current-node new-context behaviors chart) :routes))]
    
    (if route (process-chart ((behaviors :set-current-node-id) new-context (route :route-destination))
                             behaviors
                             chart)
        new-context)))