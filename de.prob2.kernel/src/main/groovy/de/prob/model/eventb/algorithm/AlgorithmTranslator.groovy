package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
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
		List<Procedure> procedures = machine.getChildrenOfType(Procedure.class)
		if (block.size() == 1) {
			machineM = translator.run(machineM, block[0], procedures)
		}
		machineM.getMachine()
	}
}
