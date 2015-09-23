package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.graph.AlgorithmGraph
import de.prob.model.eventb.algorithm.graph.AlgorithmToGraph
import de.prob.model.eventb.algorithm.graph.BranchCondition
import de.prob.model.eventb.algorithm.graph.EventInfo

class MergeAllBranches implements ITranslationAlgorithm {

	def MachineModifier run(MachineModifier machineM, Block b) {
		def namectr = 0
		machineM = machineM.addComment(new AlgorithmPrettyPrinter(b).prettyPrint())
		machineM = machineM.var_block("pc", "pc : NAT", "pc := 0")

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
		machineM
	}
}
