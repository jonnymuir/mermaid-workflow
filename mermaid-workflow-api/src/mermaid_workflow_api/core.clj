(ns mermaid-workflow-api.core
  (:require [mermaid-workflow-api.server :as server]))

(defn -main 
  "The entry point for the Mermaid Workflow API server.
  
    This function starts an HTTP server using the Http-kit server library, serving the
    `app` defined in the `mermaid-workflow-api.routes` namespace.
  
    ARGS:
    - [& [port]] An optional port number as a string. If no port is provided, the server defaults the system env PORT or to port 3000 if not present."
  [& [port]]
  (let [server-port (if port (Integer. port) (or (System/getenv "PORT") "3000"))] ;; Default port to 3000 if not provided
    (server/start server-port)))
