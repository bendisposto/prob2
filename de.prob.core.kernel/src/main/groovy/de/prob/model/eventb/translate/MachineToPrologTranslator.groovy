package de.prob.model.eventb.translate

import de.be4.classicalb.core.parser.node.AAnticipatedEventstatus
import de.be4.classicalb.core.parser.node.AConvergentEventstatus
import de.be4.classicalb.core.parser.node.AEvent
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit
import de.be4.classicalb.core.parser.node.AEventsModelClause
import de.be4.classicalb.core.parser.node.AInvariantModelClause
import de.be4.classicalb.core.parser.node.AOrdinaryEventstatus
import de.be4.classicalb.core.parser.node.ARefinesModelClause
import de.be4.classicalb.core.parser.node.ASeesModelClause
import de.be4.classicalb.core.parser.node.ATheoremsModelClause
import de.be4.classicalb.core.parser.node.AVariablesMachineClause
import de.be4.classicalb.core.parser.node.AVariantModelClause
import de.be4.classicalb.core.parser.node.TIdentifierLiteral
import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.representation.Machine

class MachineToPrologTranslator {

	def AEventBModelParseUnit model = new AEventBModelParseUnit();
	def EventBMachine machine

	def MachineToPrologTranslator(EventBMachine m) {
		machine = m
	}

	def translateMachine() {
		model.setName(machine.getName())
		def clauses = []

		clauses << processContexts()

		def refines = processRefines()
		if(refines != null) {
			clauses << refines
		}

		clauses << processVariables()
		clauses << processInvariants()
		clauses << processTheorems()

		def variant = processVariant()
		if(variant != null) {
			clauses << variant
		}
		clauses << processEvents()
	}

	def processVariables() {
		def ids = []
		machine.getVariables().each {
			ids << it.getExpression().ast
		}
		return new AVariablesMachineClause(ids)
	}

	def processInvariants() {
		def invs = []
		machine.getInvariants().each {
			if(!it.isTheorem()) {
				invs << it.getPredicate().ast
			}
		}
		return new AInvariantModelClause(invs)
	}

	def processTheorems() {
		def thms = []
		machine.getInvariants().each {
			if(it.isTheorem()) {
				thms << it.getPredicate().ast
			}
		}
		return new ATheoremsModelClause(thms)
	}

	def processVariant() {
		if(machine.getVariant() != null) {
			return new AVariantModelClause(machine.getVariant().getExpression().ast)
		}
		return null
	}

	def processContexts() {
		def contexts = machine.getChildrenOfType(Context.class);
		def contextNames = []
		contexts.each {
			contextNames << new TIdentifierLiteral(it.getName())
		}
		return new ASeesModelClause(contextNames)
	}

	def processRefines() {
		def refines = machine.getChildrenOfType(Machine.class)
		if(!refines.isEmpty()) {
			return new ARefinesModelClause(refines[0].getName())
		}
		return null
	}

	def processEvents() {
		def events = []
		machine.getEvents().each {
			def event = new AEvent()
			event.setEventName(new TIdentifierLiteral(it.getName()))
			event.setStatus(extractEventStatus(it))

			def refined = []
			it.getRefines().each {
				refined << new TIdentifierLiteral(it.getName())
			}
			event.setRefines(refined)

			def params = []
			it.getParameters().each {
				params << it.getExpression().ast
			}
			event.setVariables(params)

			def guards = []
			def theorems = []
			it.getGuards().each {
				if(it.isTheorem()) {
					theorems << it.getPredicate().ast
				} else {
					guards << it.getPredicate().ast
				}
			}
			event.setGuards(guards)
			event.setTheorems(theorems)

			def witnesses = []
			it.getWitness().each {
			}
		}
		return new AEventsModelClause(events)
	}

	def extractEventStatus(Event e) {
		def res
		switch(e.getType()) {
			case Event.EventType.ORDINARY:
				res = new AOrdinaryEventstatus()
				break
			case Event.EventType.ANTICIPATED:
				res = new AAnticipatedEventstatus()
				break
			case Event.EventType.CONVERGENT:
				res = new AConvergentEventstatus()
				break
		}
		return res
	}
}
