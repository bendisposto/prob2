# Development Setup

cljx autocompiler:
`lein with-profile +dev cljx auto`

figwheel:
`rlwrap lein figwheel`

repl:
`lein repl` 

Starting the system:
 - In the Clojure repl: `(go)`
 - this creates system stored in user/system 
 
Browser: `http://localhost:3000` 

# Structure of the client side 

```
cljs
├── routing.cljs      	   <--- entry point, plus history
├── dataflow.cljs     	   <--- subscription handlers  (query layer)
├── core.cljs         	   <--- reagent main component (view layer)
├── components        	   <--- reagent components (view layer)
│  	├── history.cljs
│   ├── state_inspector.cljs   	   
│   └── trace_selection.cljs
└── event_handler.cljs     <--- event handlers (control/update layer)
```