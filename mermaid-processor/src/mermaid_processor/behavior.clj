(ns mermaid-processor.behavior)

(defn get-current-node-id [context chart]
  (or (context :current-node-id) (chart :start-at)))

(defn set-current-node-id [context chart node-id]
  (-> context
      (assoc :current-node-id node-id)
      (update :path-taken
              (fn [path-taken]
                (if (nil? path-taken)
                  [(get-current-node-id context chart) node-id] ; creates a new vector if path-taken is nil
                  (conj path-taken node-id))))))

(defn audit [context chart action result]
  (update
   context
   :audit
   (fn [audit]
     (let [new-audit {:node (get-current-node-id context chart)
                      :action action
                      :action-result result}]
       (if (nil? audit)
         [new-audit]
         (conj audit new-audit))))))

(defn build [actions]
  {:actions actions
   :set-current-node-id set-current-node-id
   :get-current-node-id get-current-node-id
   :audit audit })