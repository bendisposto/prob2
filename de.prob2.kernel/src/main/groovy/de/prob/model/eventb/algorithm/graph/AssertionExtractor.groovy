package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

class AssertionExtractor {
	Block algorithm
	Map<Statement, Set<Assertion>> assertions

	def AssertionExtractor(Block algorithm) {
		assertions = new LinkedHashMap()
		this.algorithm = eliminateAssertions(algorithm)
	}

	def addAssertions(Statement stmt, Set<Assertion> stmts) {
		if (assertions[stmt] == null) {
			assertions[stmt] = stmts
		} else {
			assertions[stmt].addAll(stmts)
		}
	}

	def Block eliminateAssertions(Block b) {
		if (b.statements.isEmpty()) {
			return b
		}
		List<Statement> stmts = eliminateAssertions(b.statements.head(), b.statements.tail())
		return new Block(stmts, b.typeEnvironment)
	}

	def List<Statement> eliminateAssertions(Assertion t, List<Statement> stmts) {
		Set<Assertion> myassertions = [t] as LinkedHashSet
		List<Statement> statements = stmts
		while (!statements.isEmpty() && statements.head() instanceof Assertion) {
			myassertions.add(statements.head())
			statements = statements.tail()
		}
		if (statements.isEmpty()) {
			Statement h = new Assignments(t.typeEnvironment)
			addAssertions(h, myassertions)
			return [h]
		}
		List<Statement> nextS = eliminateAssertions(statements.head(), statements.tail())
		assert !nextS.isEmpty() && !(nextS.first() instanceof Assertion)
		addAssertions(nextS.first(), myassertions)
		nextS
	}

	def List<Statement> eliminateAssertions(While w, List<Statement> stmts) {
		While newWhile = w.updateBlock(eliminateAssertions(w.block))
		recurIfNecessary(newWhile, stmts)
	}

	def List<Statement> eliminateAssertions(Assignments a, List<Statement> stmts) {
		recurIfNecessary(a, stmts)
	}

	def List<Statement> eliminateAssertions(If i, List<Statement> stmts) {
		If newI = i.newIf(eliminateAssertions(i.getThen()), eliminateAssertions(i.getElse()))
		recurIfNecessary(newI, stmts)
	}

	def List<Statement> recurIfNecessary(Statement s, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return [s]
		}
		def retStmts = [s]
		retStmts.addAll(eliminateAssertions(stmts.head(), stmts.tail()))
		return retStmts
	}
}
