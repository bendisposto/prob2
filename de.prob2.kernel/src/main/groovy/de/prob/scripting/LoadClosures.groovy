package de.prob.scripting

import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.translate.EventBModelTranslator
import de.prob.model.representation.AbstractModel
import de.prob.statespace.StateSpace

class LoadClosures {

	def static Closure<Object> EMPTY = {AbstractModel model -> }

	def static Closure<Object> EVENTB =  {EventBModel model ->
		def vars = new HashSet<String>()
		def s = model as StateSpace
		def mt = new EventBModelTranslator(model)
		mt.extractMachineHierarchy(model).reverse().each {
			it.getVariables().each {
				if (!vars.contains(it.getName())) {
					it.subscribe(s)
				}
				vars << it.getName()
			}
		}
		def cs = new HashSet<String>()
		mt.extractContextHierarchy(model).reverse().each {
			it.getConstants().each {
				if (!cs.contains(it.getName())) {
					it.subscribe(s)
				}
				cs << it.getName()
			}
		}
	}

	def static Closure<Object> B = {ClassicalBModel model ->
		model.getMainMachine().getVariables().each {
			it.subscribe(model as StateSpace)
		}
	}
}
