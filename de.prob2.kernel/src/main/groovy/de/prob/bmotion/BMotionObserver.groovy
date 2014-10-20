package de.prob.bmotion;

abstract class BMotionObserver {

	def abstract apply(BMotion bms)

    def BMotionObserver register(BMotion bms) {
        bms.registerObserver(this)
    }

}