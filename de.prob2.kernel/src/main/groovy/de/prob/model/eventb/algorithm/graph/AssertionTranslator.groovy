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
import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.ast.transform.AssertionExtractor
import de.prob.model.eventb.algorithm.ast.transform.AssertionPropagator
import de.prob.util.Tuple2

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
	Map<Statement, List<Assertion>> properties
	Map<Statement, List<Tuple2<List<EventB>,EventB>>> propagated
	def NodeNaming names
	String pcname

	def AssertionTranslator(MachineModifier machineM, List<Procedure> procedures, ControlFlowGraph graph, Map<Statement, Integer> pcInfo, AlgorithmGenerationOptions options, String pcname) {
		this.graph = graph
		this.machineM = machineM
		this.pcInfo = pcInfo
		this.pcname = pcname
		this.properties = new AssertionExtractor().extractAssertions(graph.algorithm)
		this.names = new NodeNaming(graph.algorithm)
		if (options.isPropagateAssertions()) {
			def p = new AssertionPropagator(procedures)
			p.traverse(graph.algorithm)
			propagated = p.assertionMap
		} else {
			propagated = [:]
		}

		visit(graph.algorithm)
	}

	@Override
	public visit(While w) {
		assert pcInfo[w] != null
		def prefix = "$pcname = ${pcInfo[w]}"
		if (w.invariant) {
			def name = names.getName(w)+"_inv"
			machineM = writeAssertion(machineM, name, prefix, w.invariant)
		}
		machineM = writeAssertions(machineM, w, prefix)
		if (propagated[w]) {
			// If you want to eliminate possible duplicate assertions:
			//def toTranslate = propagated[w].findAll { !optimized || it.getFirst() != [w.notCondition]}
			//machineM = writePropagated(machineM, toTranslate, prefix)
			machineM = writePropagated(machineM, propagated[w], prefix)
		}
	}

	@Override
	public visit(If stmt) {
		if (pcInfo[stmt] != null) {
			def prefix = "$pcname = ${pcInfo[stmt]}"
			machineM = writeAssertions(machineM, stmt, prefix)
			if (propagated[stmt]) {
				machineM = writePropagated(machineM, propagated[stmt], prefix)
			}
		} else if (properties[stmt] != null) {
			// only enter this loop when there are actually assertions to print. performance reasons
			Set<Edge> allEdges = graph.edges
			Set<List<EventB>> conds = [] as Set
			allEdges.findAll { Edge e -> e.conditions.contains(new Tuple2<Statement, EventB>(stmt, stmt.condition)) }.each { Edge e ->
				def i = e.conditions.indexOf(new Tuple2<Statement, EventB>(stmt, stmt.condition))
				def cond = e.conditions[0..i-1].collect { it.getSecond() }
				if (!conds.contains(cond)) {
					conds << cond
					def pred = "$pcname = ${pcInfo[e.from]}"
					def rcond = cond.collect { it.getCode() }.iterator().join(" & ")
					pred = rcond = "" ? pred : "$pred & $rcond"
					machineM = writeAssertions(machineM, stmt, pred)
				}
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
		if (pcInfo[a] != null) {
			def prefix = "$pcname = ${pcInfo[a]}"
			machineM = writeAssertions(machineM, a, prefix)
			if (propagated[a]) {
				machineM = writePropagated(machineM, propagated[a], prefix)
			}
		}
	}

	def forSingleSimpleStatement(IAssignment s) {
		if (pcInfo[s] != null) {
			def prefix = "$pcname = ${pcInfo[s]}"
			machineM = writeAssertions(machineM, s,prefix)
			if (propagated[s]) {
				machineM = writePropagated(machineM, propagated[s], prefix)
			}
		} else if (properties[s] != null) {
			// only enter this loop when there are actually assertions to print. performance reasons
			Set<Edge> allEdges = graph.edges
			Set<List<EventB>> conds = [] as Set
			allEdges.findAll { Edge e -> e.assignment == s }.each { Edge e ->
				def cond = e.conditions.collect { it.getSecond() }
				if (!conds.contains(cond)) {
					conds << cond
					def pred = "$pcname = ${pcInfo[e.from]}"
					def rcond = cond.collect { it.getCode() }.iterator().join(" & ")
					pred = rcond = "" ? pred : "$pred & $rcond"
					machineM = writeAssertions(machineM, s, pred)
				}
			}
		}
	}

	@Override
	public visit(Assertion a) {
		// do nothing
	}

	def MachineModifier writeAssertions(MachineModifier machineM, Statement stmt, String pred) {
		properties[stmt].each { Assertion a ->
			machineM = writeProperty(machineM, a, pred)
		}
		machineM
	}

	def String getName(String name) {
		if (assertCtr[name] != null) {
			return name + "_" + assertCtr[name]++
		} else {
			assertCtr[name] = 0
		}
		name
	}

	def MachineModifier writeProperty(MachineModifier machineM, Assertion property, String pred) {
		def name = names.getName(property)
		writeAssertion(machineM, name, pred, property.assertion)
	}

	def MachineModifier writeAssertion(MachineModifier machineM, String name, String prefix, EventB predicate) {
		def ast = predicate.getAst()
		def formula = ast instanceof AImplicationPredicate ? "$prefix & ${predicate.getCode()}" : "$prefix => (${predicate.getCode()})"
		machineM = machineM.invariant(getName(name), formula)
	}

	def MachineModifier writePropagated(MachineModifier machineM, List<Tuple2<List<EventB>,EventB>> properties, String prefix) {
		properties.inject(machineM) { MachineModifier mm, Tuple2<List<EventB>,EventB> prop ->
			def preds = [prefix]+ prop.getFirst().collect { it.getCode() }
			def formula = preds.iterator().join(" & ") + " => (${prop.getSecond().getCode()})"
			mm.invariant(getName("assert_gen"), formula)
		}
	}


}
