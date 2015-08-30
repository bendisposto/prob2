package de.prob.model.eventb.algorithm

import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.graph.AlgorithmGraph
import de.prob.model.eventb.algorithm.graph.AlgorithmToGraph
import de.prob.model.eventb.algorithm.graph.Edge
import de.prob.model.eventb.algorithm.graph.GraphOptimizer
import de.prob.model.representation.Machine

class AlgorithmTranslator2 {
	def EventBModel model
	def MachineModifier machineM

	def AlgorithmTranslator(EventBModel model) {
		this.model = model
	}

	def EventBModel run() {
		model.getMachines().each { oldM ->
			model = model.replaceIn(Machine.class, oldM, runAlgorithm(oldM))
		}
		model
	}

	def EventBMachine runAlgorithm(EventBMachine machine) {
		def nextpc = 0
		machineM = new MachineModifier(machine)
		List<Block> block = machine.getChildrenOfType(Block.class)
		if (block.size() == 1) {
			machineM = machineM.var_block("pc", "pc : NAT", "pc := 0")
			translate(block[0])
		}
	}

	def translate(Block b) {
		AlgorithmGraph g = new AlgorithmGraph(new GraphOptimizer(new AlgorithmToGraph(b).getNode()).getAlgorithm())
		Set<Edge> done = new HashSet<Edge>()
		for (int i; i < g.size(); i++) {
			Set<Edge> edges = g.getOutEdges(i)
			edges.each { Edge e ->
				done.add(e)
			}
		}
	}
}
