# mermaid-processor

The main logic for processing mermaid diagrams.

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar mermaid-processor-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs and Issues

...

## Design guidelines

Use TDD to promote a modular design that promotes simplicity over complexity

Order function parameters by specificity (general to specific), e.g. my-function [context behavior item]

Aim towards a DDD like Ubiquitous Language. This means entities should use real life terms from the business / user domain. This language should be refleced in the code. And on language, a decision has been made to not use british english, but stick with american english, e.g. Behavoir, not behavoir. This is simply to make it consitent with other code bases.

```mermaid
C4Container
    title Container diagram for Workflow Automation System

    Person_Ext(user, "User", "Uses the WorkflowUI to interact with the system.")

    System_Boundary(workflowSys, "Workflow System") {
        Container(webUI, "WorkflowUI", "Web Application", "Provides a reference front-end for the workflow.")
        Container(webAPI, "WorkflowAPI", "Web API", "Handles the business logic and data processing for workflows.")
        ContainerDb(clojars, "Clojars", "Artifact Repository", "Stores the Clojure libraries used in the system.")
        Container(mermaidLib, "mermaid-processing", "Clojure Library", "Processes and renders diagrams.")
    }

    Rel(user, webUI, "Uses", "HTTP/HTTPS")
    Rel(webUI, webAPI, "Sends requests to", "HTTP/HTTPS")
    Rel(webAPI, clojars, "Fetches libraries from")
    Rel(webAPI, mermaidLib, "Uses for diagram processing")

    UpdateElementStyle(user, $fontColor="blue")
    UpdateRelStyle(webUI, webAPI, $textColor="green", $lineColor="green")
    UpdateRelStyle(webAPI, clojars, $textColor="purple", $lineColor="purple")
    UpdateRelStyle(webAPI, mermaidLib, $textColor="orange", $lineColor="orange")
```
```mermaid
C4Component
    title Component diagram for mermaid-processing library

    Boundary(mermaidLib, "mermaid-processing") {
        Component(context, "Context", "Stores the state of the running workflow, current node, and other state required by conditions or actions.")
        Component(behavior, "Behavior", "Specifies actions and conditions. Can modify the Context.")
        Component(chart, "Chart", "Represents the parsed Mermaid diagram.")
    }

    Rel(behavior, context, "Modifies")
    Rel(behavior, chart, "Reads state from")
    Rel(chart, context, "Updates state in")
```