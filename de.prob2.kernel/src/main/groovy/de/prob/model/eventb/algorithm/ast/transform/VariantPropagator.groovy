package de.prob.model.eventb.algorithm.ast.transform

import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.algorithm.Procedure
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.graph.NodeNaming
import de.prob.model.representation.ModelElementList
import de.prob.util.Tuple2

class VariantPropagator  {

	def FormulaUtil fuu
	def Map<Statement, List<VariantAssertion>> assertionMap = [:]
	def ModelElementList<Procedure> procedures
	def NodeNaming mapping

	def VariantPropagator(ModelElementList<Procedure> procedures, NodeNaming mapping) {
		this.fuu = new FormulaUtil()
		this.procedures = procedures
		this.mapping = mapping
	}

	public traverse(Block algorithm) {
		traverseBlock(algorithm, [])
	}

	public traverseBlock(Block b, List<VariantAssertion> toPropagate) {
		if (!b.statements.isEmpty()) {
			List<Statement> stmts = []
			stmts.addAll(b.statements)
			Collections.reverse(stmts)
			traverse(stmts.first(), toPropagate, stmts.tail())
		}
	}

	public traverse(Assignment a, List<VariantAssertion> toPropagate, List<Statement> rest) {
		recurAndCache(a, applyAssignment(toPropagate, a.assignment), rest)
	}

	public traverse(Skip a, List<VariantAssertion> toPropagate, List<Statement> rest) {
		recurAndCache(a, toPropagate, rest)
	}

	public traverse(Return a, List<VariantAssertion> toPropagate, List<Statement> rest) {
		normalRecur(a, [], rest) // cannot propagate assertions up
	}

	public traverse(Call a, List<VariantAssertion> toPropagate, List<Statement> rest) {
		Procedure p = procedures.getElement(a.getName())
		Map<String,EventB> subs = a.getSubstitutions(p)
		List<EventB> assignments = fuu.predicateToAssignments(p.postcondition, p.arguments as Set, p.results as Set).collect {
			fuu.substitute(it, subs)
		}
		def newPreds = assignments.inject(toPropagate) { props, EventB assignment ->
			applyAssignment(props, assignment)
		}
		recurAndCache(a, newPreds, rest)
	}

	public traverse(Assertion a, List<VariantAssertion> toPropagate, List<Statement> rest) {
		recurAndCache(a, toPropagate, rest)
	}

	public traverse(While w, List<VariantAssertion> toPropagate, List<Statement> rest) {
		def formulas = toPropagate
		if (w.variant) {
			formulas = formulas + [
				new VariantAssertion(mapping.getName(w), w)
			]
		}

		traverseBlock(w.block, formulas)

		List<VariantAssertion> l1 = getAssertionsForBlock(w.block, []).findAll { it.stmt != w }
		recurAndCache(w, merge(w.condition, l1, w.notCondition, toPropagate), rest)
	}

	public traverse(If i, List<VariantAssertion> toPropagate, List<Statement> rest) {
		traverseBlock(i.Then, toPropagate)
		traverseBlock(i.Else, toPropagate)
		def l1 = getAssertionsForBlock(i.Then, toPropagate)
		def l2 = getAssertionsForBlock(i.Else, toPropagate)
		recurAndCache(i, merge(i.condition, l1, i.elseCondition, l2), rest)
	}

	public List<VariantAssertion> merge(EventB condition1, List<VariantAssertion> list1, EventB condition2, List<VariantAssertion> list2) {
		List<VariantAssertion> list = list1.collect { VariantAssertion a ->
			if (list2.contains(a)) {
				return a
			}
			return a.addCondition(condition1)
		}
		list2.findAll { !list1.contains(it) }.inject(list) { l, VariantAssertion a ->
			l << a.addCondition(condition2)
			l
		}
	}

	public List<VariantAssertion> getAssertionsForBlock(Block block, List<VariantAssertion> defaultL) {
		if (block.statements.isEmpty()) {
			return defaultL
		}
		def head = block.statements.first()
		def tail = block.statements.tail()
		while (head instanceof Assertion) {
			if (tail.isEmpty()) {
				return defaultL
			}
			head = tail.first()
			tail = tail.tail()
		}
		assertionMap[head]
	}

	private List<VariantAssertion> applyAssignment(List<VariantAssertion> preds, EventB assignment) {
		if (assignment.getAst() instanceof ABecomesSuchSubstitution ||
		assignment.getAst() instanceof ABecomesElementOfSubstitution) {
			return ((preds.collect { it.stmt } as LinkedHashSet) as List).collect { While w ->
				new VariantAssertion(mapping.getName(w), w)
			}
		}

		preds.collect { VariantAssertion v ->
			v.applyAssignment(assignment)
		}
	}

	private recurAndCache(Statement s, List<Tuple2<List<EventB>,EventB>> assertions, List<Statement> rest) {
		assertionMap[s] = assertions
		if (rest.isEmpty()) {
			return rest
		} else {
			traverse(rest.first(), assertions, rest.tail())
		}
	}

	private List<Statement> normalRecur(Statement s, List<Tuple2<List<EventB>,EventB>> assertions, List<Statement> rest) {
		if (rest.isEmpty()) {
			return rest
		} else {
			traverse(rest.first(), assertions, rest.tail())
		}
	}
}
