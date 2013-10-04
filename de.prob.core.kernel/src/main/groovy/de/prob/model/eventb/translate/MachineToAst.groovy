package de.prob.model.eventb.translate

import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
import de.be4.classicalb.core.parser.node.AVariablesModelClause
import de.be4.classicalb.core.parser.node.AVariantModelClause
import de.be4.classicalb.core.parser.node.AWitness
import de.be4.classicalb.core.parser.node.Node
import de.be4.classicalb.core.parser.node.TIdentifierLiteral
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.representation.Invariant
import de.prob.prolog.output.IPrologTermOutput

class MachineToAst {

	def EventBMachine machine
	Logger logger = LoggerFactory.getLogger(MachineToAst.class)

	def MachineToAst(EventBMachine m) {
		machine = m
	}

	def Node translateMachine() {
		def AEventBModelParseUnit ast = new AEventBModelParseUnit();
		ast.setName(new TIdentifierLiteral(machine.getName()))
		def clauses = []

		clauses << processContexts()

		def refines = processRefines()
		if(refines != null) {
			clauses << refines
		}

		clauses << processVariables()
		clauses.addAll(processInvariantsAndTheorems())

		def variant = processVariant()
		if(variant != null) {
			clauses << variant
		}
		clauses << processEvents()

		ast.setModelClauses(clauses)
		return ast
	}

	def processVariables() {
		def ids = machine.getVariables().collect {
			it.getExpression().getAst()
		}
		return new AVariablesModelClause(ids)
	}

	def processInvariantsAndTheorems() {
		def invs = []
		def thms = []
		machine.getChildrenOfType(Invariant.class).each {
			if(!it.isTheorem()) {
				invs << it.getPredicate().getAst()
			} else {
				thms << it.getPredicate().getAst()
			}
		}
		return [
			new AInvariantModelClause(invs),
			new ATheoremsModelClause(thms)
		]
	}

	def processVariant() {
		if(machine.getVariant() != null) {
			return new AVariantModelClause(machine.getVariant().getExpression().getAst())
		}
		return null
	}

	def processContexts() {
		def contexts = machine.getSees()
		def contextNames = []
		contexts.each {
			contextNames << new TIdentifierLiteral(it.getName())
		}
		return new ASeesModelClause(contextNames)
	}

	def processRefines() {
		def refines = machine.getRefines()
		if(!refines.isEmpty()) {
			return new ARefinesModelClause(new TIdentifierLiteral(refines[0].getName()))
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
				params << it.getExpression().getAst()
			}
			event.setVariables(params)

			def guards = []
			def theorems = []
			it.getGuards().each {
				if(it.isTheorem()) {
					theorems << it.getPredicate().getAst()
				} else {
					guards << it.getPredicate().getAst()
				}
			}
			event.setGuards(guards)
			event.setTheorems(theorems)

			def witnesses = []
			it.getWitnesses().each {
				witnesses << new AWitness(new TIdentifierLiteral(it.getName()), it.getPredicate().getAst())
			}
			event.setWitness(witnesses)

			def actions = []
			it.getActions().each {
				actions << it.getCode().getAst()
			}
			event.setAssignments(actions)
			events << event
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

	def printProofsToProlog(IPrologTermOutput pto) {
		machine.getProofs().each {
			pto.openTerm("po")
			pto.printAtom(machine.getName())
			pto.printAtom(it.getDescription())
			pto.openList()
			it.toProlog(pto)
			pto.closeList()
			pto.printAtom(String.valueOf(it.isDischarged()))
			pto.closeTerm()
		}
	}
}
