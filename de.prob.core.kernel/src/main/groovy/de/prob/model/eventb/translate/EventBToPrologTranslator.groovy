package de.prob.model.eventb.translate

import de.prob.model.eventb.EventBModel

class EventBToPrologTranslator {
	EventBModel model
	String mainName

	def EventBToPrologTranslator(EventBModel model) {
		this.model = model
		this.mainName = model.getMainComponentName()
	}
}
