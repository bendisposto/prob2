package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.Call
import de.prob.model.eventb.algorithm.IAlgorithmASTTransformer
import de.prob.model.eventb.algorithm.IProperty
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Return
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

/**
 * Extracts assertions and assumptions from within the AST, adding nodes after
 * the assertion/assumption if necessary.
 * @author joy
 *
 */
class PropertyExtractor implements IAlgorithmASTTransformer{

	Map<Statement, Set<IProperty>> properties = [:]

	@Override
	public Block transform(Block algorithm) {
		extractAssertions(algorithm)
	}

	def addAssertions(Statement stmt, List<Assertion> stmts) {
		if (properties[stmt] == null) {
			properties[stmt] = stmts as LinkedHashSet
		} else {
			properties[stmt].addAll(stmts)
		}
	}

	def Block extractAssertions(Block b) {
		if (b.statements.isEmpty()) {
			return b
		}
		List<Statement> stmts = extractAssertions(b.statements.head(), b.statements.tail())
		return new Block(stmts, b.typeEnvironment)
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
		List<Statement> nextS = extractAssertions(statements.head(), statements.tail())
		assert !nextS.isEmpty() && !(nextS.first() instanceof IProperty)
		addAssertions(nextS.first(), myproperties)
		myproperties + nextS
	}

	def List<Statement> extractAssertions(While w, List<Statement> stmts) {
		While newWhile = w.updateBlock(extractAssertions(w.block))
		recurIfNecessary(newWhile, stmts)
	}

	def List<Statement> extractAssertions(Assignments a, List<Statement> stmts) {
		recurIfNecessary(a, stmts)
	}

	def List<Statement> extractAssertions(Call a, List<Statement> stmts) {
		recurIfNecessary(a, stmts)
	}

	def List<Statement> extractAssertions(Return a, List<Statement> stmts) {
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
