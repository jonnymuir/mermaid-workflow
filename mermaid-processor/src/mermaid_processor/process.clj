(ns mermaid-processor.process)

(defn get-current-node [chart context]
  ((chart :nodes) (or (context :current-node-id) (chart :start-at)))
  )

(defn get-current-node-text [chart context]
  (let [current-node (get-current-node chart context)]
    (current-node :node-text)))

(defn process-routes [routes behaviors context]
  (some (fn [route]
          (println route)
          (when-let [route-text (:route-text route)]
            (if-let [condition-fn ((:conditions behaviors) route-text)]
              (when (condition-fn context)
                route)
              (throw (ex-info (str "Condition not found: " route-text)
                              {:route-text route-text :context context})))))
        routes))

(defn run-action [action behaviors context]
  (let [action-fn ((behaviors :actions) action)]
    (if action-fn
      (action-fn context)
      (throw (ex-info (str "Action not found: " action)
                      {:action action :context context})))))

(defn process-chart [chart behaviors context]
  (let [new-context (run-action (get-current-node-text chart context)
                                  behaviors
                                  context)
        route (process-routes ((get-current-node chart new-context) :routes)
                              behaviors
                              new-context)]
    
    (if route (process-chart chart 
                             behaviors 
                             (assoc new-context
                                    :current-node-id
                                    (route :route-destination)))
        new-context)))
                 
