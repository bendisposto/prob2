package de.prob.scripting

import de.prob.model.eventb.translate.EventBModelTranslator
import de.prob.statespace.StateSpace

class LoadClosures {

	def static Closure<Object> EMPTY = {StateSpace s -> }

	def static Closure<Object> EVENTB =  {StateSpace s ->
		def vars = new HashSet<String>()
		def mt = new EventBModelTranslator(s.getModel(), s.getMainComponent())
		mt.extractMachineHierarchy(s.getModel()).reverse().each {
			it.getVariables().each {
				if (!vars.contains(it.getName())) {
					it.subscribe(s)
				}
				vars << it.getName()
			}
		}
		def cs = new HashSet<String>()
		mt.extractContextHierarchy(s.getModel()).reverse().each {
			it.getConstants().each {
				if (!cs.contains(it.getName())) {
					it.subscribe(s)
				}
				cs << it.getName()
			}
		}
	}

	def static Closure<Object> B = {StateSpace s ->
		s.getModel().getMainMachine().getVariables().each { it.subscribe(s) }
	}
}
