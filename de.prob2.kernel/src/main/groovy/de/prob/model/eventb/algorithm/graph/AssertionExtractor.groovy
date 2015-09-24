package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

class AssertionExtractor implements IAlgorithmASTTransformer{

	Map<Statement, Set<Assertion>> assertions = [:]

	@Override
	public Block transform(Block algorithm) {
		extractAssertions(algorithm)
	}

	def addAssertions(Statement stmt, List<Assertion> stmts) {
		if (assertions[stmt] == null) {
			assertions[stmt] = stmts as LinkedHashSet
		} else {
			assertions[stmt].addAll(stmts)
		}
	}

	def Block extractAssertions(Block b) {
		if (b.statements.isEmpty()) {
			return b
		}
		List<Statement> stmts = extractAssertions(b.statements.head(), b.statements.tail())
		return new Block(stmts, b.typeEnvironment)
	}

	def List<Statement> extractAssertions(Assertion t, List<Statement> stmts) {
		List<Assertion> myassertions = [t]
		List<Statement> statements = stmts
		while (!statements.isEmpty() && statements.head() instanceof Assertion) {
			myassertions << statements.head()
			statements = statements.tail()
		}
		if (statements.isEmpty()) {
			Statement h = new Assignments(t.typeEnvironment)
			addAssertions(h, myassertions)
			return myassertions + [h]
		}
		List<Statement> nextS = extractAssertions(statements.head(), statements.tail())
		assert !nextS.isEmpty() && !(nextS.first() instanceof Assertion)
		addAssertions(nextS.first(), myassertions)
		myassertions + nextS
	}

	def List<Statement> extractAssertions(While w, List<Statement> stmts) {
		While newWhile = w.updateBlock(extractAssertions(w.block))
		recurIfNecessary(newWhile, stmts)
	}

	def List<Statement> extractAssertions(Assignments a, List<Statement> stmts) {
		recurIfNecessary(a, stmts)
	}

	def List<Statement> extractAssertions(If i, List<Statement> stmts) {
		If newI = i.newIf(extractAssertions(i.getThen()), extractAssertions(i.getElse()))
		recurIfNecessary(newI, stmts)
	}

	def List<Statement> recurIfNecessary(Statement s, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return [s]
		}
		def retStmts = [s]
		retStmts.addAll(extractAssertions(stmts.head(), stmts.tail()))
		return retStmts
	}
}
