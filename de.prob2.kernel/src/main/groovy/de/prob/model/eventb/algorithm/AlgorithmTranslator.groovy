package de.prob.model.eventb.algorithm

import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.transform.AssertionPropagator
import de.prob.model.eventb.algorithm.ast.transform.DeadCodeRemover
import de.prob.model.eventb.algorithm.ast.transform.IAlgorithmASTTransformer
import de.prob.model.representation.ModelElementList

class AlgorithmTranslator {
	def ModelModifier modelM
	def AlgorithmGenerationOptions options

	def AlgorithmTranslator(EventBModel model, AlgorithmGenerationOptions options) {
		this.modelM = new ModelModifier(model)
		this.options = options
	}

	def AlgorithmTranslator(ModelModifier modelM, AlgorithmGenerationOptions options) {
		this.modelM = modelM
		this.options = options
	}

	def EventBModel run() {
		ModelElementList<Procedure> procedures = modelM.getModel().getChildrenOfType(Procedure.class)
		modelM.getModel().getMachines().collect { it.getName() }.each { name ->
			EventBMachine oldM = modelM.getModel().getMachine(name)
			EventBMachine newMachine = translate(oldM, procedures)
			modelM = modelM.replaceMachine(oldM, newMachine)
		}
		modelM.getModel()
	}

	def EventBMachine translate(EventBMachine machine, ModelElementList<Procedure> procedures) {
		def machineM = new MachineModifier(machine, modelM.typeEnvironment)
		List<Procedure> proc = machine.getChildrenOfType(Procedure.class)
		if (proc.size() > 0) {
			if (machine.getName().endsWith(Procedure.ABSTRACT_SUFFIX)) {
				return machineM.addEvent(proc[0].getEvent()).getMachine()
			}
		}

		List<Block> block = machine.getChildrenOfType(Block.class)
		if (block.size() == 1) {
			Block b = runASTTransformations(block[0], procedures)
			machineM = new TranslationAlgorithm(options, procedures, proc.size() > 0 ? "ipc" : "pc").run(machineM, b)
		}
		machineM.getMachine()
	}

	def Block runASTTransformations(Block block, ModelElementList<Procedure> procedures) {
		def transformers = [new DeadCodeRemover()]
		if (options.isPropagateAssertions()) {
			transformers << new AssertionPropagator(procedures)
		}
		transformers.inject(block) { Block b, IAlgorithmASTTransformer t ->
			t.transform(b)
		}
	}
}
