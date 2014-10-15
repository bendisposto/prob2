package de.prob.bmotion

import com.google.gson.Gson
import org.jsoup.nodes.Element

class ReactTransformerObserver implements IBMotionObserver {

    def Gson g = new Gson()

    def List<IBMotionTransformer> transformers = []

    def String id

    def Element element

    private def selectorCache = [:]

    def ReactTransformerObserver(String id, Element element) {
        this.id = id
        this.element = element
    }


    def List<String> getCachedBmsId(String selector) {
        def t = selector.isEmpty() ? [] : selectorCache.get(selector)
        if (t == null) {
            t = element.select(selector).collect { it.attr("data-bmsid") }
            selectorCache.put(selector, t)
        }
        t
    }

    @Override
    def apply(BMotion bms) {
        def map = [:]
        transformers.each { TransformerObject t ->
            getCachedBmsId(t.selector).each {
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