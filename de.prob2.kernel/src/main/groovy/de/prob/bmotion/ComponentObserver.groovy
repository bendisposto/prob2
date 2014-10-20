package de.prob.bmotion

class ComponentObserver extends TransformersObserver {

    def String component

    private def selectorCache = [:]

    def ComponentObserver() {
    }

    def ComponentObserver(String component) {
        this.component = component
    }

    def static ComponentObserver make(Closure cls) {
        new ComponentObserver().with cls
    }

    def ComponentObserver component(String component) {
        this.component = component
        this
    }

    def private List<String> getCachedBmsId(BMotion bms, String selector) {
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
        transformers.each { BMotionTransformer o ->
            TransformerObject t = o.update(bms)
            getCachedBmsId(bms, t.selector).each {
                def ReactTransform rt = map.get(it)
                if (rt == null) {
                    rt = new ReactTransform(it)
                    map.put(it, rt)
                }
                rt.attributes.putAll(t.attributes)
                rt.styles.putAll(t.styles)
                rt.content = t.content
            }
        }
        bms.submit([cmd: "bmotion_om.core.setComponent", type: "probmotion-html", id: component, observers: map])
    }

}