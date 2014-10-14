package de.prob.bmotion

import com.google.gson.Gson
import org.jsoup.nodes.Element

abstract class ProBMotionComponent {

    def Element element
    def String id
    def List<BMotionObserver> observers = []
    protected final Gson g = new Gson()

    def ProBMotionComponent() {
    }

    def ProBMotionComponent(String id, Element element) {
        this.id = id
        this.element = element
    }

    def abstract init(BMotion bms)

    def abstract apply(BMotion bms, List<BMotionObserver> observers)

    def List<Transform> getObserverTransformers(BMotion bms, List<BMotionObserver> observers) {
        observers.collectMany { it.update(bms) }
    }

}
