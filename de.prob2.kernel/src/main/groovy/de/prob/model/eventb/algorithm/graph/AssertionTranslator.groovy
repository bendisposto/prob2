package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.AlgorithmASTVisitor
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Assumption
import de.prob.model.eventb.algorithm.IProperty
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

/**
 * Translates assertions and assumptions
 * @author joy
 *
 */
class AssertionTranslator extends AlgorithmASTVisitor {

	def MachineModifier machineM
	def ControlFlowGraph graph
	def Map<Statement, Integer> pcInfo
	Map<String, Integer> assertCtr = [:]
	boolean optimized

	def AssertionTranslator(MachineModifier machineM, ControlFlowGraph graph, Map<Statement, Integer> pcInfo, boolean optimized) {
		this.graph = graph
		this.machineM = machineM
		this.pcInfo = pcInfo
		this.optimized = optimized
		visit(graph.algorithm)
	}

	@Override
	public visit(While w) {
		assert pcInfo[w] != null
		machineM = writeAssertions(machineM, w, "pc = ${pcInfo[w]}")
	}

	@Override
	public visit(If stmt) {
		if (pcInfo[stmt] != null) {
			machineM = writeAssertions(machineM, stmt, "pc = ${pcInfo[stmt]}")
		} else if (graph.properties[stmt] != null) {
			// only enter this loop when there are actually assertions to print. performance reasons
			Set<Edge> allEdges = [] as Set
			graph.outgoingEdges.inject(allEdges) { Set<Edge> all, entry -> all.addAll(entry.value); all }
			Set<List<EventB>> conds = [] as Set
			allEdges.findAll { Edge e -> e.conditions.contains(stmt.condition) }.each { Edge e ->
				def i = e.conditions.indexOf(stmt.condition)
				def cond = e.conditions[0..i-1]
				if (!conds.contains(cond)) {
					conds << cond
					def pred = "pc = ${pcInfo[e.from]}"
					def rcond = cond.collect { it.getCode() }.iterator().join(" & ")
					pred = rcond = "" ? pred : "$pred & $rcond"
					machineM = writeAssertions(machineM, stmt, pred)
				}
			}
		}
	}

	@Override
	public visit(Assignments a) {
		if (!optimized) {
			assert pcInfo[a] != null
			machineM = writeAssertions(machineM, a, "pc = ${pcInfo[a]}")
		} else {
			Set<Edge> inE = graph.inEdges(a)
			inE.each { Edge e ->
				if (e.conditions.isEmpty()) {
					assert pcInfo[a] != null
					machineM = writeAssertions(machineM, a, "pc = ${pcInfo[a]}")
				} else {
					def pred = "pc = ${pcInfo[e.from]}"
					def rcond = e.conditions.collect { it.getCode() }.iterator().join(" & ")
					machineM = writeAssertions(machineM, a, "$pred & $rcond")
				}
			}
		}
	}

	@Override
	public visit(Assertion a) {
		// do nothing
	}

	@Override
	public visit(Assumption a) {
		// do nothing
	}

	def MachineModifier writeAssertions(MachineModifier machineM, Statement stmt, String pred) {
		graph.properties[stmt].each { IProperty p ->
			machineM = writeProperty(machineM, p, pred)
		}
		machineM
	}

	def MachineModifier writeProperty(MachineModifier machineM, IProperty property, String pred) {
		def name = graph.nodeMapping.getName(property)
		if (assertCtr[name] != null) {
			name = name + "_" + assertCtr[name]++
		} else {
			assertCtr[name] = 0
		}
		machineM = machineM.invariant(name, "$pred => (${property.getFormula().getCode()})", property instanceof Assumption, property.toString())
	}


}
