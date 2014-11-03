package de.prob.bmotion

import de.prob.animator.domainobjects.EvalResult
import de.prob.statespace.OpInfo
import de.prob.statespace.Trace
import groovy.transform.TupleConstructor

@TupleConstructor
class CSPTraceObserver extends BMotionObserver {

    def String _component

    private objs = []

    private selectorCache = [:]

    def static CSPTraceObserver make(Closure cls) {
        new CSPTraceObserver().with cls
    }

    def CSPTraceObserver component(component) {
        this._component = component
        this
    }

    def CSPTraceObserver observe(String exp, Closure cls) {
        def evt = new EventsObserver(exp).with cls
        objs.add(evt)
        this
    }

    def static getOpString(OpInfo op) {
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
            t = bms.components.get(_component).element.select(selector).collect { it.attr("data-bmsid") }
            selectorCache.put(selector, t)
        }
        t
    }

    @Override
    def apply(BMotion bms) {
        def map = [:]

        def Trace trace = bms.getTool().getTrace()
        trace.ensureOpInfosEvaluated()

        trace.getCurrent().getOpList().each { OpInfo op ->

            def fullOp = getOpString(op)
            objs.each { EventsObserver evt ->

                def events = bms.eval(evt.exp)

                if (events instanceof EvalResult) {
                    def eventNames = events.value.replace("{", "").replace("}", "").split(",")
                    if (eventNames.contains(fullOp)) {

                        evt.transformers.each { TransformerObserver gt ->

                            def fselector = (gt._selector instanceof Closure) ? gt._selector(op) : gt._selector
                            def fattributes = gt._attributes.collectEntries { kv ->
                                (kv.value instanceof Closure) ? [kv.key, kv.value(op)] : [kv.key, kv.value
                                ]
                            }
                            def fstyles = gt._styles.collectEntries { kv ->
                                (kv.value instanceof Closure) ? [kv.key, kv.value(op)] : [kv.key, kv.value
                                ]
                            }
                            def fcontent = (gt._content instanceof Closure) ? gt._content(op) : gt._content

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

        bms.submit([cmd: "bmotion_om.core.setComponent", type: "probmotion-html", id: _component, observers: map])

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