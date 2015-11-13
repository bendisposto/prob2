package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement

/**
 * Extracts assertions and assumptions from within the AST, adding nodes after
 * the assertion/assumption if necessary.
 * @author joy
 *
 */
class AssertionExtractor extends AlgorithmASTTransformer {

	Map<Statement, Set<Assertion>> properties = [:]

	def addAssertions(Statement stmt, List<Assertion> stmts) {
		if (properties[stmt] == null) {
			properties[stmt] = stmts as LinkedHashSet
		} else {
			properties[stmt].addAll(stmts)
		}
	}

	@Override
	def List<Statement> transform(Assertion a, List<Statement> rest) {
		extractAssertions(a, rest)
	}

	def List<Statement> extractAssertions(Assertion t, List<Statement> stmts) {
		List<Assertion> myproperties = [t]
		List<Statement> statements = stmts
		while (!statements.isEmpty() && statements.head() instanceof Assertion) {
			myproperties << statements.head()
			statements = statements.tail()
		}
		if (statements.isEmpty()) {
			Statement h = new Skip(t.typeEnvironment)
			addAssertions(h, myproperties)
			return myproperties + [h]
		}
		List<Statement> nextS = transform(statements.head(), statements.tail())
		assert !nextS.isEmpty() && !(nextS.first() instanceof Assertion)
		addAssertions(nextS.first(), myproperties)
		myproperties + nextS
	}
}
