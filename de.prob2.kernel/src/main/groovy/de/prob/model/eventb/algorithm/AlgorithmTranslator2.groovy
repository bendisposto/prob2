package de.prob.model.eventb.algorithm

import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.graph.AlgorithmGraph
import de.prob.model.eventb.algorithm.graph.AlgorithmToGraph
import de.prob.model.eventb.algorithm.graph.BranchCondition
import de.prob.model.eventb.algorithm.graph.EventInfo
import de.prob.model.eventb.algorithm.graph.GraphOptimizer
import de.prob.model.representation.Machine

class AlgorithmTranslator2 {
	def EventBModel model
	def MachineModifier machineM
	def namectr = 0

	def AlgorithmTranslator2(EventBModel model) {
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
		machineM.getMachine()
	}

	def translate(Block b) {
		AlgorithmGraph g = new AlgorithmGraph(new GraphOptimizer(new AlgorithmToGraph(b).getNode()).getAlgorithm())
		g.assertions.each { pc, Assertion assertion ->
			machineM = machineM.invariant("pc = $pc => ${assertion.assertion}")
		}
		g.nodes.each { EventInfo ev ->
			machineM = machineM.event(name: "evt${namectr++}") {
				Map<Integer, BranchCondition> bcs = ev.conditions
				if (bcs.size() == 1) {
					bcs.each { pc, BranchCondition cond ->
						guard("pc = $pc")
						cond.getConditions().each { guard(it) }
					}
				} else {
					guard(bcs.collect { pc, BranchCondition cond ->
						def conditions = cond.getConditions()
						conditions ? "(pc = $pc & ${conditions.iterator().join(' & ')})" : "(pc = $pc)"
					}.iterator().join(' or '))
				}
				ev.actions.each { Assignments a ->
					a.assignments.each { String assign ->
						action(assign)
					}
				}
			}
		}
	}
}
