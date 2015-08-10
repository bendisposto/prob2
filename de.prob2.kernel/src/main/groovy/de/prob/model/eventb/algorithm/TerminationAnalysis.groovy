package de.prob.model.eventb.algorithm

import de.prob.animator.command.CbcSolveCommand
import de.prob.animator.domainobjects.EvalResult
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.Variant
import de.prob.model.eventb.Event.EventType
import de.prob.statespace.StateSpace

class TerminationAnalysis {
	ModelModifier modelM

	def TerminationAnalysis(EventBModel model) {
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
		modelM = modelM.machine(name: baseName) {
			event(name: loopInfo.lastStatement.getName(), type: EventType.ANTICIPATED) {}
		}

		def refinementName = "${baseName}_loop${loopInfo.startPc}"
		modelM = modelM.refine(baseName, refinementName)

		modelM = modelM.machine(name: refinementName, refines: baseName) {
			variable "var"
			invariant typingVar: typingVariant
			initialisation(extended: true) {
				then init
			}
			variant "var"
			refine(name: loopInfo.lastStatement.getName(), type: EventType.CONVERGENT, extended: true) {
				then variant: "var := ${loopInfo.variant.getExpression().getCode()}"
			}
		}

		modelM
	}

	def String typingForVariant(EventBMachine machine, StateSpace s, Variant variant) {
		assert machine.variables.var == null

		def invs = machine.invariants.collect { it.getPredicate().getCode() }.iterator().join(" & ")
		def pred = "${invs} & var = ${variant.getExpression().getCode()}"

		def result = cbc(s, pred).translate().var
		if (result instanceof de.prob.translator.types.BigInteger) {
			return "var : INT"
		}
		throw new IllegalArgumentException("Type "+result.getClass()+" is not currently supported for variant type.")
	}

	def String initForVariant(EventBMachine machine, StateSpace s, Variant variant) {
		assert machine.variables.var == null

		def init = machine.events.INITIALISATION
		def assignments = init.getActions().collect { it.getCode().getCode().replace(":=","=") }.join(' & ')
		def pred = "${assignments} & var = ${variant.getExpression().getCode()}"
		"var := ${cbc(s, pred).var}"
	}

	def EvalResult cbc(StateSpace s, String pred) {
		def cmd = new CbcSolveCommand(s.getModel().parseFormula(pred))
		s.execute(cmd)
		cmd.getValue()
	}

}
