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
	def MachineModifier machineM
	def namectr = 0

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
			machineM = machineM.addComment(new AlgorithmPrettyPrinter(block[0]).prettyPrint())
			machineM = machineM.var_block("pc", "pc : NAT", "pc := 0")
			translate(block[0])
		}
		machineM.getMachine()
	}

	def translate(Block b) {
		AlgorithmGraph g = new AlgorithmGraph(new AlgorithmToGraph(b).getNode())
		g.assertions.each { pc, List<Assertion> assertion ->
			assertion.each { a ->
				machineM = machineM.invariant("pc = $pc => ${a.assertion.getCode()}")
			}
		}
		g.nodes.each { EventInfo ev ->
			Map<Integer, BranchCondition> bcs = ev.conditions
			bcs.each { pc, BranchCondition cond ->
				machineM = machineM.event(name: "evt${namectr++}") {
					def ctr = 0
					guard("grd${ctr++}","pc = $pc")
					cond.condAndStmts().each { guard("grd${ctr++}", it.getFirst(), false, it.getSecond().toString()) }
					ev.actions.each { Assignments a ->
						a.assignments.each { EventB assign ->
							action(assign)
						}
					}
				}
			}
		}
	}
}
