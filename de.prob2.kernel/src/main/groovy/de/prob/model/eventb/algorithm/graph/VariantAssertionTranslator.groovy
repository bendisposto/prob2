package de.prob.model.eventb.algorithm.graph

import de.be4.classicalb.core.parser.node.AImplicationPredicate
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.Procedure
import de.prob.model.eventb.algorithm.ast.AlgorithmASTVisitor
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.ast.transform.VariantAssertion
import de.prob.model.eventb.algorithm.ast.transform.VariantPropagator
import de.prob.util.Tuple2

/**
 * Translates assertions and assumptions
 * @author joy
 *
 */
class VariantAssertionTranslator extends AlgorithmASTVisitor {

	def MachineModifier machineM
	def ControlFlowGraph graph
	def Map<Statement, Integer> pcInfo
	Map<String, Integer> assertCtr = [:]
	Map<Statement, List<VariantAssertion>> propagated
	boolean optimized
	String pcname

	def VariantAssertionTranslator(MachineModifier machineM, List<Procedure> procedures, ControlFlowGraph graph, Map<Statement, Integer> pcInfo, AlgorithmGenerationOptions options, String pcname) {
		this.graph = graph
		this.machineM = machineM
		this.pcInfo = pcInfo
		this.optimized = options.isOptimize()
		this.pcname = pcname
		def p = new VariantPropagator(procedures, graph.nodeMapping)
		p.traverse(graph.algorithm)
		this.propagated = p.assertionMap

		visit(graph.algorithm)
	}

	@Override
	public visit(While w) {
		assert pcInfo[w] != null
		def prefix = "$pcname = ${pcInfo[w]}"
		if (propagated[w]) {
			machineM = writePropagated(machineM, propagated[w], prefix)
		}
	}

	@Override
	public visit(If stmt) {
		if (pcInfo[stmt] != null) {
			def prefix = "$pcname = ${pcInfo[stmt]}"
			if (propagated[stmt]) {
				machineM = writePropagated(machineM, propagated[stmt], prefix)
			}
		}
	}

	@Override
	public visit(Assignment a) {
		forSingleSimpleStatement(a)
	}

	@Override
	public visit(Call a) {
		forSingleSimpleStatement(a)
	}

	@Override
	public visit(Return a) {
		forSingleSimpleStatement(a)
	}

	@Override
	public Object visit(Skip a) {
		forSingleSimpleStatement(a)
	}

	def forSingleSimpleStatement(Statement s) {
		if (!optimized || graph.inEdges(s).isEmpty()) {
			assert pcInfo[s] != null
			def prefix = "$pcname = ${pcInfo[s]}"
			if (propagated[s]) {
				machineM = writePropagated(machineM, propagated[s], prefix)
			}
		} else {
			Set<Edge> inE = graph.inEdges(s)
			inE.each { Edge e ->
				if (e.conditions.isEmpty()) {
					assert pcInfo[s] != null
					def prefix = "$pcname = ${pcInfo[s]}"
					if (propagated[s]) {
						machineM = writePropagated(machineM, propagated[s], prefix)
					}
				} else {
					def pred = "$pcname = ${pcInfo[e.from]}"
					def rcond = e.conditions.collect { it.getCode() }.iterator().join(" & ")
					if (propagated[s]) {
						machineM = writePropagated(machineM, propagated[s], "$pred & $rcond")
					}
				}
			}
		}
	}

	@Override
	public visit(Assertion a) {
		// do nothing
	}

	def String getName(String name) {
		if (assertCtr[name] != null) {
			return name + "_" + assertCtr[name]++
		} else {
			assertCtr[name] = 0
		}
		name
	}

	def MachineModifier writePropagated(MachineModifier machineM, List<VariantAssertion> properties, String prefix) {
		properties.inject(machineM) { MachineModifier mm, VariantAssertion prop ->
			def preds = [prefix]+ prop.conditions.collect { it.getCode() }
			def formula1 = preds.iterator().join(" & ") + " => (${prop.variantCondition.getCode()})"
			def formula2 = preds.iterator().join(" & ") + " => (${prop.positive.getCode()})"
			mm.invariant(getName("variant_assert"), formula1).invariant(getName("variant_assert"), formula2)
		}
	}
}
