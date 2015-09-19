package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.MachineModifier;
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.TranslationAlgorithm;

class NaiveAlgorithmGenerator extends TranslationAlgorithm {

	ControlFlowGraph graph
	Map<Statement, Integer> pcInformation

	def NaiveAlgorithmGenerator(Block b) {
		graph = new ControlFlowGraph(b)
		pcInformation = new PCCalculator(graph).pcInformation
	}

	@Override
	public MachineModifier run(MachineModifier machineM, Block algorithm) {
		return machineM
	}
}
