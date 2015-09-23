package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.graph.AlgorithmGraph
import de.prob.model.eventb.algorithm.graph.AlgorithmToGraph
import de.prob.model.eventb.algorithm.graph.BranchCondition
import de.prob.model.eventb.algorithm.graph.EventInfo
import de.prob.model.representation.Machine

class AlgorithmTranslator {
	def EventBModel model
	def ITranslationAlgorithm translator

	def AlgorithmTranslator(EventBModel model, ITranslationAlgorithm translator) {
		this.model = model
		this.translator = translator
	}

	def EventBModel run() {
		model.getMachines().each { oldM ->
			model = model.replaceIn(Machine.class, oldM, translate(oldM))
		}
		model
	}

	def EventBMachine translate(EventBMachine machine) {
		def machineM = new MachineModifier(machine)
		List<Block> block = machine.getChildrenOfType(Block.class)
		if (block.size() == 1) {
			// Might need to change? Should there be more than one block? Perhaps for procedure definitions?
			machineM = translator.run(machineM, block[0])
		}
		machineM.getMachine()
	}
}
