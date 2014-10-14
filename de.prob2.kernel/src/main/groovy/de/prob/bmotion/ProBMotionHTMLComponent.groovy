package de.prob.bmotion

import de.prob.web.WebUtils
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor

class ProBMotionHTMLComponent extends ProBMotionComponent {

    private def selectorCache = [:]

    def ProBMotionHTMLComponent(String id, Element element) {
        super(id, element)
    }

    def init(BMotion bms) {
        element.traverse(new NodeVisitor() {
            int counter = 1;

            public void head(Node node, int depth) {
                node.attr("data-bmsid", "bms" + String.valueOf(counter));
                counter++;
            }

            public void tail(Node node, int depth) {
            }
        });
        bms.submit(WebUtils.wrap("cmd", "bms.initComponent", "type", "probmotion-html", "id", id, "html", element.html()))
    }

    def List<String> getCachedBmsId(String selector) {
        def t = selector.isEmpty() ? [] : selectorCache.get(selector)
        if (t == null) {
            t = element.select(selector).collect { it.attr("data-bmsid") }
            selectorCache.put(selector, t)
        }
        t
    }

    def apply(BMotion bms, List<BMotionObserver> observers) {
        def transformers = getObserverTransformers(bms, observers)
        def applymap = [:]
        transformers.each { t ->
            getCachedBmsId(t.selector).each {
                def rt = applymap.get(it)
                if(rt == null) {
                    rt = new ReactTransform(it)
                    applymap.put(it,rt)
                }
                rt.attributes.putAll(t.attributes)
                rt.styles.putAll(t.styles)
                rt.content = t.content
            }
        }
        bms.submit([cmd:"bms.setComponent",type:"probmotion-html",id:id,observers:applymap])
    }

}
