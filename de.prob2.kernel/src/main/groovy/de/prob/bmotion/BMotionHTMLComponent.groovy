package de.prob.bmotion

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor

class BMotionHTMLComponent extends BMotionComponent {

    def BMotionHTMLComponent(String id, Element element) {
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
        bms.submit([cmd: "bmotion_om.core.initComponent", type: "probmotion-html", id: id, html: element.html()])
    }

}
