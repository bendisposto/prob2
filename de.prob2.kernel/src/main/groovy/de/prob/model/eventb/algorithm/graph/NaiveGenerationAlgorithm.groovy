package de.prob.model.eventb.algorithm.graph

import java.util.Map;

import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.Block;
import de.prob.model.eventb.algorithm.Statement;
import de.prob.model.eventb.algorithm.TranslationAlgorithm;

class NaiveGenerationAlgorithm extends TranslationAlgorithm {

	ControlFlowGraph graph
	Map<Statement, Integer> pcInformation
	Set<Statement> generated = [] as Set

	@Override
	public MachineModifier run(MachineModifier machineM, Block algorithm) {
		graph = new ControlFlowGraph(algorithm)
		pcInformation = new PCCalculator(graph).pcInformation
		println graph.incomingEdges
	}
}
