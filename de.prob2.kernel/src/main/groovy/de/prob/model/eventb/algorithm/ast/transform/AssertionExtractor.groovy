package de.prob.model.eventb.algorithm.ast.transform

import com.github.krukow.clj_lang.PersistentHashMap
import com.github.krukow.clj_lang.PersistentVector

import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

/**
 * Extracts assertions and assumptions from within the AST, adding nodes after
 * the assertion/assumption if necessary.
 * @author joy
 *
 */
class AssertionExtractor {

	def PersistentHashMap<Statement, List<Assertion>> extractAssertions(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			return extractAssertions(PersistentHashMap.emptyMap(), algorithm.statements)
		}
		PersistentHashMap.emptyMap()
	}

	def extractAssertions(PersistentHashMap assertions, List<Statement> statements) {
		PersistentVector<Assertion> a = PersistentVector.emptyVector()
		while (!statements.isEmpty() && statements.head() instanceof Assertion) {
			a = a.plus(statements.head())
			statements = statements.tail()
		}
		if (statements.isEmpty()) {
			assert a.isEmpty() // a must be mapped to a statement!
			return assertions
		}
		def s = statements.head()
		assertions = assertions.plus(statements.head(), a)
		if (s instanceof While) {
			assertions = extractAssertions(assertions, s.block.statements)
		}
		if (s instanceof If) {
			assertions = extractAssertions(assertions, s.Then.statements)
			assertions = extractAssertions(assertions, s.Else.statements)
		}
		extractAssertions(assertions, statements.tail())
	}
}
