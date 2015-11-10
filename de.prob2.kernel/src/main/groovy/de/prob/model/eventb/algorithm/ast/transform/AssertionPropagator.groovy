package de.prob.model.eventb.algorithm.ast.transform

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.IProperty
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.representation.ModelElementList
import de.prob.util.Tuple2

class AssertionPropagator implements IAlgorithmASTTransformer {

	def FormulaUtil fuu
	def Map<Statement, List<Tuple2<List<EventB>, EventB>>> assertionMap = [:]

	def AssertionPropagator() {
		this.fuu = new FormulaUtil()
	}

	@Override
	public Block transform(Block algorithm) {
		transformBlock(algorithm, [])
	}

	public Block transformBlock(Block b, List<Tuple2<List<EventB>,EventB>> toPropagate) {
		if (b.statements.isEmpty()) {
			return b
		}
		List<Statement> stmts = []
		stmts.addAll(b.statements)
		Collections.reverse(stmts)
		stmts = transform(stmts.first(), toPropagate, stmts.tail())
		Collections.reverse(stmts)
		return new Block(stmts, b.typeEnvironment)
	}

	public List<Statement> transform(Assignment a, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		def newPreds = toAssertions(toPropagate).collect {
			new Tuple2<List<EventB>, EventB>([], fuu.applyAssignment(it.getAssertion(), a.assignment))
		}
		recurAndCache(a, newPreds, rest)
	}

	public List<Statement> transform(Assertion a, List<Tuple2<List<EventB>, EventB>> toPropagate, List<Statement> rest) {
		def stmts = [a]
		if (rest) {
			stmts.addAll(transform(rest.first(), toPropagate, rest.tail()))
		}
		stmts
	}

	public List<Statement> transform(While w, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		While newWhile = w.updateBlock(transformBlock(w.block, copyAndAdd(toPropagate, w.invariant)))

		List<Tuple2<List<EventB>,EventB>> prop = toPropagate.inject([]) { acc, Tuple2<List<EventB>,EventB> f ->
			def l = [newWhile.notCondition]
			l.addAll(f.getFirst())
			acc << new Tuple2<List<EventB>, EventB>(l, f.getSecond())
		}
		prop.addAll(getAssertionsForHead(newWhile.block.statements, newWhile.condition, []))
		recurAndCache(newWhile, prop, rest)
	}

	public List<Statement> transform(If i, List<Tuple2<List<EventB>,EventB>> toPropagate, List<Statement> rest) {
		If newIf = i.newIf(transformBlock(i.Then, copyAndAdd(toPropagate)), transformBlock(i.Else, copyAndAdd(toPropagate)))
		List<Tuple2<List<EventB>,EventB>> prop = getAssertionsForHead(newIf.Else.statements, newIf.elseCondition, toPropagate)
		prop.addAll(getAssertionsForHead(newIf.Then.statements, newIf.condition, toPropagate))
		recurAndCache(newIf, prop, rest)
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
		while (head instanceof IProperty) {
			if (tail.isEmpty()) {
				return addCondition(newCondition, defaultL)
			}
			head = tail.first()
			tail = tail.tail()
		}
		addCondition(newCondition, assertionMap[head])
	}

	public List<Tuple2<List<EventB>, EventB>> addCondition(EventB condition, List<Tuple2<List<EventB>, EventB>> list) {
		list.collect { Tuple2<List<EventB>, EventB> f ->
			def newcond = [condition]
			newcond.addAll(f.getFirst())
			new Tuple2<List<EventB>, EventB>(newcond, f.getSecond())
		}
	}

	private List<Assertion> toAssertions(List<Tuple2<List<EventB>, EventB>> assertions) {
		assertions.collect { Tuple2<List<EventB>, EventB> tuple ->
			if (tuple.getFirst().isEmpty()) {
				return new Assertion(tuple.getSecond())
			}
			String lhs = tuple.getFirst().collect { it.getCode() }.iterator().join(" & ")
			EventB pred = new EventB(lhs + " => ("+tuple.getSecond().getCode()+")", tuple.getSecond().getTypes())
			new Assertion(pred)
		}
	}

	private List<Statement> recurAndCache(Statement s, List<Tuple2<List<EventB>,EventB>> assertions, List<Statement> rest) {
		def stmts = [s]
		stmts.addAll(toAssertions(assertions))
		assertionMap[s] = assertions
		if (rest) {
			stmts.addAll(transform(rest.first(), assertions, rest.tail()))
		}
		stmts
	}
}
