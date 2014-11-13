package de.prob.bmotion

class ComponentObserver extends TransformersObserver {

    def String component

    private def selectorCache = [:]

    def ComponentObserver() {}

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
        transformers.each { TransformerObserver gt ->
            def list = gt.update(bms)
            list.each { TransformerObject to ->
                getCachedBmsId(bms, to.selector).each {
                    def ReactTransform rt = map.get(it)
                    if (rt == null) {
                        rt = new ReactTransform(it)
                        map.put(it, rt)
                    }
                    rt.attributes.putAll(to.attributes)
                    rt.styles.putAll(to.styles)
                    rt.content = to.content
                }
            }
        }
        System.out.println(map)
        bms.submit([cmd: "bmotion_om.core.setComponent", type: "probmotion-html", id: component, observers: map])
    }

}