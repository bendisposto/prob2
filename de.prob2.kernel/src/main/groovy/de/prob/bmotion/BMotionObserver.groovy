package de.prob.bmotion;

import java.util.List;
import de.prob.bmotion.SelectorTransformer

abstract class BMotionObserver {
	
	def abstract List<SelectorTransformer> update(BMotion bms)
	
}