package de.prob.bmotion;

import java.util.List;
import de.prob.bmotion.Transform

abstract class BMotionObserver {
	
	def abstract List<Transform> update(BMotion bms)
	
}