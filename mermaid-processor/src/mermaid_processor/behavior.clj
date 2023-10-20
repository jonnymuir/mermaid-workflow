(ns mermaid-processor.behavior)

(defn build [actions conditions]
  {:actions actions
   :conditions conditions
   :set-current-node-id (fn [context node-id] (assoc context :current-node-id node-id))
   :get-current-node-id (fn [context chart] (or (context :current-node-id) (chart :start-at)))})