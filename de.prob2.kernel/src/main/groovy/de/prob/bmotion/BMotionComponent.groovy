package de.prob.bmotion

import org.jsoup.nodes.Element

abstract class BMotionComponent {

    def Element element
    def String id
    def List<BMotionObserver> observers = []

    def BMotionComponent() {
    }

    def BMotionComponent(String id, Element element) {
        this.id = id
        this.element = element
    }

    def abstract init(BMotion bms)

    def apply(BMotion bms) {
        observers.each { it.apply(bms) }
    }

    def registerObserver(BMotionObserver o) {
        observers.add(o)
    }

    def registerObserver(List<BMotionObserver> o) {
        o.each { observers.add(it) }
    }

}
