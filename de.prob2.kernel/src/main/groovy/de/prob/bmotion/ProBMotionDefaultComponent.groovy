package de.prob.bmotion

import de.prob.web.WebUtils

class ProBMotionDefaultComponent extends ProBMotionComponent {

    def ProBMotionDefaultComponent() {
    }

    def init(BMotion bms) {
    }

    def apply(BMotion bms, List<BMotionObserver> observers) {
        String json = g.toJson(getObserverTransformers(bms, observers))
        bms.submit(WebUtils.wrap("cmd", "bms.applyTransformers", "transformers", json))
    }

}
