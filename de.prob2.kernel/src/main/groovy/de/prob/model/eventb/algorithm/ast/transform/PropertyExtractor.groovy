package de.prob.model.eventb.algorithm.ast.transform

import java.util.List;

import de.prob.model.eventb.algorithm.ast.Assertion;
import de.prob.model.eventb.algorithm.ast.Assignments;
import de.prob.model.eventb.algorithm.ast.Assumption;
import de.prob.model.eventb.algorithm.ast.Block;
import de.prob.model.eventb.algorithm.ast.Call;
import de.prob.model.eventb.algorithm.ast.IProperty;
import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Return;
import de.prob.model.eventb.algorithm.ast.Statement;
import de.prob.model.eventb.algorithm.ast.While;

/**
 * Extracts assertions and assumptions from within the AST, adding nodes after
 * the assertion/assumption if necessary.
 * @author joy
 *
 */
class PropertyExtractor extends AlgorithmASTTransformer {

	Map<Statement, Set<IProperty>> properties = [:]

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

	@Override
	def List<Statement> transform(Assumption a, List<Statement> rest) {
		extractAssertions(a, rest)
	}

	def List<Statement> extractAssertions(IProperty t, List<Statement> stmts) {
		List<IProperty> myproperties = [t]
		List<Statement> statements = stmts
		while (!statements.isEmpty() && statements.head() instanceof IProperty) {
			myproperties << statements.head()
			statements = statements.tail()
		}
		if (statements.isEmpty()) {
			Statement h = new Assignments(t.typeEnvironment)
			addAssertions(h, myproperties)
			return myproperties + [h]
		}
		List<Statement> nextS = transform(statements.head(), statements.tail())
		assert !nextS.isEmpty() && !(nextS.first() instanceof IProperty)
		addAssertions(nextS.first(), myproperties)
		myproperties + nextS
	}
}
