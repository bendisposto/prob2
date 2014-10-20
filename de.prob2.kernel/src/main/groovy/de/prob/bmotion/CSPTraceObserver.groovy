package de.prob.bmotion

import de.prob.animator.domainobjects.EvalResult
import de.prob.statespace.OpInfo
import de.prob.statespace.Trace

class CSPTraceObserver extends BMotionObserver {

    def List<EventsObserver> objs = []

    def String component

    private def selectorCache = [:]

    def CSPTraceObserver(String component) {
        this.component = component
    }

    def CSPTraceObserver() {
    }

    def static CSPTraceObserver make(Closure cls) {
        new CSPTraceObserver().with cls
    }

    def CSPTraceObserver component(component) {
        this.component = component
        this
    }

    def CSPTraceObserver observe(String exp, Closure cls) {
        EventsObserver evt = new EventsObserver(exp).with cls
        objs.add(evt)
        this
    }

    def getOpString(OpInfo op) {
        def String opName = op.getName()
        def String AsImplodedString = ""
        def List<String> opParameter = op.getParams()
        if (opParameter.size() > 0) {
            String[] inputArray = opParameter.toArray(new String[opParameter
                    .size()]);
            StringBuffer sb = new StringBuffer();
            sb.append(inputArray[0]);
            for (int i = 1; i < inputArray.length; i++) {
                sb.append(".");
                sb.append(inputArray[i]);
            }
            AsImplodedString = "." + sb.toString();
        }
        String opNameWithParameter = opName + AsImplodedString;
        return opNameWithParameter;
    }

    def List<String> getCachedBmsId(BMotion bms, String selector) {
        def t = selector.isEmpty() ? [] : selectorCache.get(selector)
        if (t == null) {
            t = bms.components.get(component).element.select(selector).collect { it.attr("data-bmsid") }
            selectorCache.put(selector, t)
        }
        t
    }

    @Override
    def apply(BMotion bms) {
        def map = [:]

        def Trace trace = bms.getTool().getTrace()
        trace.ensureOpInfosEvaluated()

        trace.getCurrent().getOpList().each { op ->

            def fullOp = getOpString(op)
            objs.each { EventsObserver evt ->

                def events = bms.eval(evt.exp)

                if (events instanceof EvalResult) {
                    def eventNames = events.value.replace("{", "").replace("}", "").split(",")
                    if (eventNames.contains(fullOp)) {

                        evt.transformers.each { TransformerObserver gt ->

                            def fselector = (gt.selector instanceof Closure) ? gt.selector(op) : gt.selector
                            def fattributes = gt.attributes.collectEntries { kv ->
                                (kv.value instanceof Closure) ? [kv.key, kv.value(op)] : [kv.key, kv.value
                                ]
                            }
                            def fstyles = gt.styles.collectEntries { kv ->
                                (kv.value instanceof Closure) ? [kv.key, kv.value(op)] : [kv.key, kv.value
                                ]
                            }
                            def fcontent = (gt.content instanceof Closure) ? gt.content(op) : gt.content

                            getCachedBmsId(bms, fselector).each {
                                def ReactTransform rt = map.get(it)
                                if (rt == null) {
                                    rt = new ReactTransform(it)
                                    map.put(it, rt)
                                }
                                rt.attributes.putAll(fattributes)
                                rt.styles.putAll(fstyles)
                                rt.content = fcontent
                            }
                        }

                    }

                }

            }

        }

        bms.submit([cmd: "bmotion_om.core.setComponent", type: "probmotion-html", id: component, observers: map])

    }

    def class EventsObserver {

        def String exp
        def List<TransformerObserver> transformers = []

        def EventsObserver(String exp) {
            this.exp = exp
        }

        def EventsObserver add(TransformerObserver transformer) {
            transformers.add(transformer)
            this
        }

    }

}