package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier
import de.prob.model.representation.Machine

class AlgorithmTranslator {
	def ModelModifier modelM
	def ITranslationAlgorithm translator

	def AlgorithmTranslator(EventBModel model, ITranslationAlgorithm translator) {
		this.modelM = new ModelModifier(model)
		this.translator = translator
	}

	def AlgorithmTranslator(ModelModifier modelM, ITranslationAlgorithm translator) {
		this.modelM = modelM
		this.translator = translator
	}

	def EventBModel run() {
		modelM.getModel().getMachines().each { oldM ->
			EventBMachine abstractM = translate(oldM)
			modelM = modelM.replaceMachine(oldM, abstractM)
			oldM.getChildrenOfType(Procedure.class).each { Procedure p ->
				def name = abstractM.getName()+"_"+p.getName()
				modelM = modelM.refine(abstractM.getName(), name)
				MachineModifier machineM = new MachineModifier(modelM.getModel().getMachine(name), modelM.typeEnvironment)
				modelM = modelM.addMachine(new ProcedureTranslator(oldM, abstractM, machineM, p).getRefinedMachine())
			}
		}
		modelM.getModel()
	}

	def EventBMachine translate(EventBMachine machine) {
		def machineM = new MachineModifier(machine, modelM.typeEnvironment)
		List<Block> block = machine.getChildrenOfType(Block.class)
		List<Procedure> procedures = machine.getChildrenOfType(Procedure.class)
		if (block.size() == 1) {
			procedures.each { final Procedure p ->
				machineM = machineM.event(name: p.getName(), comment: p.toString()) {
					guard p.getPrecondition()
					action p.getAbstraction()
				}
			}
			machineM = translator.run(machineM, block[0], procedures)
		}
		machineM.getMachine()
	}
}
