package de.prob.model.eventb.algorithm

import de.be4.classicalb.core.parser.node.AAssignSubstitution
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBAction
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventModifier
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.graph.GraphMerge
import de.prob.model.eventb.algorithm.graph.OptimizedGenerationAlgorithm

class ProcedureTranslator {

	EventBMachine refinedMachine
	final FormulaUtil forU

	def ProcedureTranslator(EventBMachine emptyM, EventBMachine abstraction, MachineModifier refinedM, Procedure procedure) {
		this.forU = new FormulaUtil()
		this.refinedMachine = runTranslator(emptyM, abstraction, refinedM, procedure)
	}

	def EventBMachine runTranslator(EventBMachine emptyM, EventBMachine abstraction, MachineModifier refinedM, Procedure procedure) {
		procedure.locals.each { l, a ->
			refinedM = refinedM.variable(l.getCode())
			List<EventB> invariants = emptyM.invariants.collect { it.getPredicate() }
			forU.formulasWith(invariants, a).collect {
				forU.substitute(it, ["${a.getCode()}": l])
			}.each { EventB f ->
				refinedM = refinedM.invariant(f.getCode())
			}
		}
		refinedM = addActions(refinedM, abstraction, procedure.locals)

		Event refinedEvent = abstraction.getEvent(procedure.getName())
		refinedM = refinedM.removeEvent(refinedM.getMachine().getEvent(procedure.getName()))

		refinedM = new OptimizedGenerationAlgorithm([new GraphMerge()], procedure, refinedEvent)
		.run(refinedM, procedure.algorithm, refinedM.getMachine().getChildrenOfType(Procedure.class))
		refinedM.getMachine()
	}

	def MachineModifier addActions(MachineModifier machineM, EventBMachine abstraction, Map<EventB, EventB> locals) {
		Event e = abstraction.events.getElement("INITIALISATION")
		Map<String, EventBAction> identifierToAction = [:]
		e.actions.each { EventBAction a ->
			AssignmentAnalysisVisitor v = new AssignmentAnalysisVisitor()
			a.getCode().getAst().apply(v)
			v.getIdentifiers().each { String id ->
				if (identifierToAction.containsKey(id)) {
					throw new IllegalArgumentException("already have a mapping for variable $id")
				}
				identifierToAction[id] = a
			}
		}

		Map<EventBAction, List<EventB>> actionToLocal = [:]
		e.actions.each { EventBAction a ->
			actionToLocal[a] = []
		}
		locals.each { EventB l, EventB a ->
			actionToLocal[identifierToAction[a.getCode()]] << l
		}
		EventModifier eventM = new EventModifier(new Event("INITIALISATION", EventType.ORDINARY, false)).refines(e, false)
		actionToLocal.each { EventBAction a, List<EventB> subs ->
			eventM = addAction(eventM, a, subs, locals)
		}
		machineM.replaceEvent(machineM.getMachine().getEvent("INITIALISATION"), eventM.getEvent())
	}

	def EventModifier addAction(EventModifier eventM, EventBAction action, List<EventB> toSubstitute, Map<EventB, EventB> locals) {
		if (toSubstitute.isEmpty()) {
			return eventM.action(action.getName(), action.getCode(), action.getComment())
		}
		if (action.getCode().getAst() instanceof AAssignSubstitution) {
			eventM = addAssignments(eventM, action, toSubstitute, locals)
		}
		if (action.getCode().getAst() instanceof ABecomesSuchSubstitution) {
			eventM = addBecomeSuchThat(eventM, action, toSubstitute, locals)
		}
		if (action.getCode().getAst() instanceof ABecomesElementOfSubstitution) {
			eventM = addBecomesElementOf(eventM, action, toSubstitute)
		}
		eventM
	}

	def EventModifier addAssignments(EventModifier eventM, EventBAction action, List<EventB> toSub, Map<EventB, EventB> locals) {
		eventM = eventM.action(action.getName(), action.getCode(), action.getComment())
		def code = action.getCode().getCode()
		assert code.contains(":=")
		def split = code.split(":=")
		assert split.length == 2

		def slhs = split[0].split(",")
		def srhs = split[1].split(",")
		assert slhs.length == srhs.length

		Map<String, String> formulas = [:]
		[slhs, srhs].transpose().each { String id, String formula ->
			formulas[id.trim()] = formula.trim()
		}

		toSub.each { EventB e ->
			assert formulas[locals[e].getCode()]
			eventM = eventM.action(action.getName()+"_"+e.getCode(), e.getCode() + " := "+formulas[locals[e].getCode()])
		}
		eventM
	}

	def EventModifier addBecomeSuchThat(EventModifier eventM, EventBAction action, List<EventB> toSub, Map<EventB, EventB> locals) {
		def code = action.getCode().getCode()
		assert code.contains(":|")
		def split = code.split(":\\|")
		assert split.length == 2
		def lhs = split[0] + toSub.collect {
			","+it.getCode()
		}.iterator().join("")
		def rhs = split[1] + toSub.collect {
			" & "+it.getCode()+"="+locals[it].getCode()+"'"
		}.iterator().join("")
		eventM.action(action.getName(), lhs+":|"+rhs, action.getComment())
	}

	def EventModifier addBecomesElementOf(EventModifier eventM, EventBAction action, List<EventB> toSub) {
		def code = action.getCode().getCode()
		assert code.contains("::")
		def split = code.split("::")
		assert split.length == 2

		def lhs = split[0].trim() + toSub.collect {
			","+it.getCode()
		}.iterator().join("")
		def primed = split[0].trim()+"'"
		def rhs = primed+":"+split[1].trim()+ toSub.collect {
			" & "+primed+"="+it.getCode()+"'"
		}.iterator().join("")
		eventM.action(action.getName(), lhs+" :| "+ rhs, action.getComment())
	}
}