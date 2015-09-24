package de.prob.model.eventb.algorithm

import de.prob.animator.command.CbcSolveCommand
import de.prob.animator.domainobjects.EvalResult
import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.Variant
import de.prob.model.eventb.Event.EventType
import de.prob.statespace.StateSpace

class NaiveTerminationAnalysis {
	ModelModifier modelM

	def NaiveTerminationAnalysis(EventBModel model) {
		this.modelM = new ModelModifier(model)
	}

	def EventBModel run() {
		modelM.getModel().getMachines().each { m ->
			modelM = forMachine(m, modelM)
		}
		modelM.getModel()
	}

	def ModelModifier forMachine(EventBMachine m, ModelModifier modelM) {
		m.getChildrenOfType(LoopInformation.class).each { loopInfo ->
			modelM = genTerminationRefinement(modelM, m, loopInfo)
		}
		modelM
	}

	def ModelModifier genTerminationRefinement(ModelModifier modelM, EventBMachine m, LoopInformation loopInfo) {
		StateSpace s = modelM.getModel().load(m)
		String typingVariant = typingForVariant(m, s, loopInfo.variant)
		String init = initForVariant(m, s, loopInfo.variant)

		def baseName = m.getName()
		// change loop event to anticipated in the refinement
		loopInfo.loopStatements.each { Event loopE ->
			modelM = modelM.machine(name: baseName) {
				event(name: loopE.getName(), type: EventType.ANTICIPATED) {}
			}
		}

		def refinementName = "${baseName}_${loopInfo.stmtName}"
		modelM = modelM.refine(baseName, refinementName)
		def comment = "Termination Proof for: \n"+new AlgorithmPrettyPrinter().prettyPrint(loopInfo.stmt)
		modelM = modelM.machine(name: refinementName, comment: comment,
		refines: baseName, sees: m.getSees().collect { it.getName() }) {
			variable "var"
			invariant typingVar: typingVariant
			initialisation(extended: true) { then init }
			variant "var"
			loopInfo.loopStatements.each { Event e ->
				refine(name: e.getName(), type: EventType.CONVERGENT, extended: true) { then variant: "var := ${loopInfo.variant.getExpression().getCode()}" }
			}
		}

		modelM
	}

	def String typingForVariant(EventBMachine machine, StateSpace s, Variant variant) {
		assert machine.variables.var == null

		def invs = machine.invariants.collect { "(${it.getPredicate().getCode()})" }.iterator().join(" & ")
		def pred = "${invs} & var = ${variant.getExpression().getCode()}"

		def result = cbc(s, pred).translate().var
		if (result instanceof de.prob.translator.types.BigInteger) {
			return "var : INT"
		}
		throw new IllegalArgumentException("Type "+result.getClass()+" is not currently supported for variant type.")
	}

	def String initForVariant(EventBMachine machine, StateSpace s, Variant variant) {
		assert machine.variables.var == null

		def axioms = machine.sees.collect { Context c ->
			c.axioms.collect { "(${it.getPredicate().getCode()})" }.iterator().join(" & ")
		}.findAll { it != "" }.iterator().join(" & ")

		axioms = axioms == "" ? "" : axioms + " & "

		def init = machine.events.INITIALISATION
		def assignments = init.getActions().collect { it.getCode().getCode().replace(":=","=") }.join(' & ')
		def pred = "$axioms ${assignments} & var = ${variant.getExpression().getCode()}"
		"var := ${cbc(s, pred).var}"
	}

	def EvalResult cbc(StateSpace s, String pred) {
		def cmd = new CbcSolveCommand(s.getModel().parseFormula(pred))
		s.execute(cmd)
		cmd.getValue()
	}

}
