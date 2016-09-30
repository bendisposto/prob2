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
import de.prob.model.representation.ModelElementList
import de.prob.util.Tuple2

class AssertionPropagator  {

	def FormulaUtil fuu
	def Map<Statement, List<Tuple2<List<EventB>, EventB>>> assertionMap = [:]
	def ModelElementList<Procedure> procedures

	def AssertionPropagator(ModelElementList<Procedure> procedures) {
		this.fuu = new FormulaUtil()
		this.procedures = procedures
	}

	public traverse(Block algorithm) {
		traverseBlock(algorithm, [])
	}

	public traverseBlock(Block b, List<Tuple2<List<EventB>,EventB>> toPropagate) {
		if (!b.statements.isEmpty()) {
			List<Statement> stmts = []
			stmts.addAll(b.statements)
			Collections.reverse(stmts)
			traverse(stmts.first(), toPropagate, stmts.tail())
		}
	}

	public traverse(Assignment a, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		recurAndCache(a, applyAssignment(toPropagate, a.assignment), rest)
	}

	public traverse(Skip a, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		recurAndCache(a, toPropagate, rest)
	}

	public traverse(Return a, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		recurAndCache(a, [], rest) // cannot propagate assertions up
	}

	public traverse(Call a, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
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

	public traverse(Assertion a, List<Tuple2<List<EventB>, EventB>> toPropagate, List<Statement> rest) {
		normalRecur(a, copyAndAdd(toPropagate, a.assertion), rest)
	}

	public traverse(While w, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		def li = []
		if (w.invariant) {
			li = copyAndAdd(li, w.invariant)
		}
		traverseBlock(w.block, li)

		List<Tuple2<List<EventB>,EventB>> prop = getAssertionsForHead(w.block.statements, w.condition, [])
		prop = toPropagate.inject(prop) { acc, Tuple2<List<EventB>,EventB> f ->
			def l = [w.notCondition]
			l.addAll(f.getFirst())
			acc << new Tuple2<List<EventB>, EventB>(l, f.getSecond())
		}
		assertionMap[w] = prop
		normalRecur(w, [], rest)
	}

	public traverse(If i, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		traverseBlock(i.Then, copyAndAdd(toPropagate))
		traverseBlock(i.Else, copyAndAdd(toPropagate))
		List<Tuple2<List<EventB>,EventB>> prop = getAssertionsForHead(i.Then.statements, i.condition, toPropagate)
		prop.addAll(getAssertionsForHead(i.Else.statements, i.elseCondition, toPropagate))
		recurAndCache(i, prop, rest)
	}

	public List<Tuple2<List<EventB>,EventB>> copyAndAdd(List<Tuple2<List<EventB>,EventB>> list, EventB... newF) {
		List<Tuple2<List<EventB>,EventB>> prop = []
		prop.addAll(list)
		prop.addAll(newF.collect {
			new Tuple2<List<EventB>, EventB>([], it)
		})
		prop
	}

	public List<Tuple2<List<EventB>, EventB>> getAssertionsForHead(List<Statement> stmts, EventB newCondition, List<Tuple2<List<EventB>,EventB>> defaultL) {
		if (stmts.isEmpty()) {
			return addCondition(newCondition, defaultL)
		}
		def head = stmts.first()
		def tail = stmts.tail()
		def newAssertions = []
		while (head instanceof Assertion) {
			newAssertions << new Tuple2<List<EventB>,EventB>([], head.assertion)
			if (tail.isEmpty()) {
				return addCondition(newCondition, newAssertions + defaultL)
			}
			head = tail.first()
			tail = tail.tail()
		}
		addCondition(newCondition, newAssertions + assertionMap[head])
	}

	public List<Tuple2<List<EventB>, EventB>> addCondition(EventB condition, List<Tuple2<List<EventB>, EventB>> list) {
		list.collect { Tuple2<List<EventB>, EventB> f ->
			def newcond = [condition]
			newcond.addAll(f.getFirst())
			new Tuple2<List<EventB>, EventB>(newcond, f.getSecond())
		}
	}

	private List<Tuple2<List<EventB>, EventB>> applyAssignment(List<Tuple2<List<EventB>, EventB>> preds, EventB assignment) {
		if (assignment.getAst() instanceof ABecomesSuchSubstitution ||
		assignment.getAst() instanceof ABecomesElementOfSubstitution) {
			return [] // nothing can be propagated up
		}
		preds.collect { Tuple2<List<EventB>, EventB> pred ->
			def conditions = pred.getFirst().collect {
				fuu.applyAssignment(it, assignment)
			}
			new Tuple2<List<EventB>, EventB>(conditions, fuu.applyAssignment(pred.getSecond(), assignment))
		}
	}

	private recurAndCache(Statement s, List<Tuple2<List<EventB>,EventB>> assertions, List<Statement> rest) {
		assertionMap[s] = assertions
		if (rest) {
			traverse(rest.first(), assertions, rest.tail())
		}
	}

	private List<Statement> normalRecur(Statement s, List<Tuple2<List<EventB>,EventB>> assertions, List<Statement> rest) {
		if (rest) {
			traverse(rest.first(), assertions, rest.tail())
		}
	}
}
