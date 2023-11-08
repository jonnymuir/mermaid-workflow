(ns mermaid-processor.process 
  (:require [mermaid-processor.behaviors.utils :as utils]
            [clojure.string :as str]))

(defn- get-current-node [context behavior chart]
  ((chart :nodes) ((behavior :get-current-node-id) context chart)))

(defn- get-current-node-text [context behavior chart]
  (let [current-node (get-current-node context behavior chart)]
    (current-node :node-text)))

(defn- process-routes [context behavior routes ]
  (some (fn [route]
          (let [route-text (route :route-text)]
            (if (str/blank? route-text)
              route
              (if-let [condition-fn ((behavior :actions) route-text)]
                (when ((condition-fn context) :result)
                  route)
                (throw (ex-info (str "Condition not found: " route-text)
                                {:route-text route-text :context context}))))))
        routes))

(defn- run-action [context behavior chart action]
  (if-let [action-fn ((behavior :actions) action)]
    (let [action-result (action-fn context)
      ;; Store the last result in fields/last-result and return the new context
      new-context (utils/set-last-result (action-result :context) (action-result :result))] 
      ((behavior :audit) new-context chart action (action-result :result)))
    (throw (ex-info (str "Action not found: " action)
                    {:action action :context context}))))

(defn process-chart 
  "Processes a mermaid chart by parsing its structure and executing the defined behaviors and conditions on its nodes.
  
    ARGUMENTS:
    - context: The runtime context containing state information required for node conditions, actions, etc.
    - behavior: An object specifying the actions and conditions. This object dictates how each node affects the context and determines the flow of the chart.
    - chart: The mermaid chart content to be processed.
  
    The function first parses the chart to obtain its structural representation, including nodes and their connections. It then iteratively evaluates each node's associated actions and conditions as defined in the behavior object, potentially altering the context and influencing the path taken through the chart.
  
    RETURN:
    A modified context object after all actions and conditions have been processed. The structure of this object will depend on the specifications in the behavior object and the initial state of the context.
    {:current-node-id \"A\"
    :fields {...} ; whatever data has been modified or added during processing
    :path-taken [...]} ; the sequence of nodes processed

   EXAMPLE:
   (process-chart 
    {:current-node nil
    :context-data {...}}
    {:actions {...}
    :conditions {...}}
    (parse/parse-mermaid \"flowchart TD
        A[Start]-->|Action1|B[Do Something]-->|Action2|C[End]\"))
    
   THROWS:
   - ExceptionInfo if there is an error in parsing the chart.
   - Custom exceptions related to the execution of actions or evaluation of conditions, as defined by the behavior object.

   NOTE:
   The behavior object must be structured in a way that the function understands, with a predefined schema for actions and conditions. 
   Improperly formatted behavior objects or chart content may result in exceptions or undefined behavior.
   You can use behavior/build to do this for you. 
    "
  [context behavior chart]
  (let [new-context (run-action context
                                behavior
                                chart
                                (get-current-node-text context behavior chart))
        route (process-routes new-context
                              behavior
                              ((get-current-node new-context behavior chart) :routes))]

    (if route (process-chart ((behavior :set-current-node-id) new-context chart (route :route-destination))
                             behavior
                             chart)
        new-context)))