package de.prob.bmotion;

import de.prob.bmotion.Transform

abstract class BMotionObserver {
	
	def final UUID uuid = UUID.randomUUID()
	
	def abstract List<Transform> update(BMotionStudioSession bms)
	
}