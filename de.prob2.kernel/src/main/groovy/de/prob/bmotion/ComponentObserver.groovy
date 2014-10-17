package de.prob.bmotion

import org.jsoup.nodes.Element

class ComponentObserver extends TransformerObserver {

    def String id

    def Element element

    private def selectorCache = [:]

    def ComponentObserver(String id) {
        this.id = id
    }

    def List<String> getCachedBmsId(BMotion bms, String selector) {
        def t = selector.isEmpty() ? [] : selectorCache.get(selector)
        if (t == null) {
            t = bms.components.get(id).element.select(selector).collect { it.attr("data-bmsid") }
            selectorCache.put(selector, t)
        }
        t
    }

    @Override
    def apply(BMotion bms) {
        def map = [:]
        transformers.each { IBMotionTransformer o ->
            TransformerObject t = o.update(bms)
            getCachedBmsId(bms,t.selector).each {
                def ReactTransform rt = map.get(it)
                if(rt == null) {
                    rt = new ReactTransform(it)
                    map.put(it,rt)
                }
                rt.attributes.putAll(t.attributes)
                rt.styles.putAll(t.styles)
                rt.content = t.content
            }
        }
        bms.submit([cmd:"bmotion_om.core.setComponent",type:"probmotion-html",id:id,observers:map])
    }

}