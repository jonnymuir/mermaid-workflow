(ns mermaid-processor.behavior)

(defn- get-current-node-id
  "Retrieves the current node ID from the provided context.
     
     ARGUMENTS:
     - context: A map containing the runtime state of the processing.
     - chart: A map representing the structure of the mermaid chart.
  
     RETURN:
     The current node ID from the context or the starting node ID from the chart if not present in the context."
  [context chart]
  (or (context :current-node-id) (chart :start-at)))

(defn- set-current-node-id 
  "Updates the context with the given node ID and appends it to the path taken.
     
     ARGUMENTS:
     - context: A map containing the runtime state of the processing.
     - chart: A map representing the structure of the mermaid chart.
     - node-id: The ID of the node to set as the current node.
  
     RETURN:
     An updated context with the new current node ID and an appended path taken."
  [context chart node-id]
  (-> context
      (assoc :current-node-id node-id)
      (update :path-taken
              (fn [path-taken]
                (if (nil? path-taken)
                  [(get-current-node-id context chart) node-id] ; creates a new vector if path-taken is nil
                  (conj path-taken node-id))))))

(defn- audit 
  "Appends an audit entry to the context detailing the action taken and its result for the current node.
     
     ARGUMENTS:
     - context: A map containing the runtime state of the processing.
     - chart: A map representing the structure of the mermaid chart.
     - action: The action that was executed.
     - result: The result of the executed action.
  
     RETURN:
     An updated context with a new audit entry added to its audit trail."
  [context chart audit-event]
  (update
   context
   :audit
   (fn [audit]
     (let [new-audit {:node (get-current-node-id context chart)
                      :audit-event audit-event}]
       (if (nil? audit)
         [new-audit]
         (conj audit new-audit))))))

(defn build 
  "Constructs a behavior map encapsulating the provided actions and utility functions.
     
     ARGUMENTS:
     - actions: A map of action functions to be included in the behavior.
  
     RETURN:
     A behavior map that includes the provided actions and utility functions for managing the current node ID and auditing actions."
  [actions]
  {:actions actions
   :set-current-node-id set-current-node-id
   :get-current-node-id get-current-node-id
   :audit audit })