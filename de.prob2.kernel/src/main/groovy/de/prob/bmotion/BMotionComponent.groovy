package de.prob.bmotion

import org.jsoup.nodes.Element

abstract class BMotionComponent {

    def Element element
    def String id

    def BMotionComponent() {
    }

    def BMotionComponent(String id, Element element) {
        this.id = id
        this.element = element
    }

    def abstract init(BMotion bms)

}
