package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class AlgorithmASTTransformer implements IAlgorithmASTTransformer {

	@Override
	public Block transform(Block algorithm) {
		if (algorithm.statements.isEmpty()) {
			return algorithm
		}
		List<Statement> stmts = transform(algorithm.statements.first(), algorithm.statements.tail())
		return new Block(stmts, algorithm.typeEnvironment)
	}

	def List<Statement> transform(While w, List<Statement> rest) {
		While newWhile = w.updateBlock(transform(w.block))
		recurIfNecessary(newWhile, rest)
	}

	def List<Statement> transform(If i, List<Statement> rest) {
		If newI = i.newIf(transform(i.getThen()), transform(i.getElse()))
		recurIfNecessary(newI, rest)
	}

	def List<Statement> transform(Assignment a, List<Statement> rest) {
		recurIfNecessary(a, rest)
	}

	def List<Statement> transform(Call a, List<Statement> rest) {
		recurIfNecessary(a, rest)
	}

	def List<Statement> transform(Return a, List<Statement> rest) {
		recurIfNecessary(a, rest)
	}

	def List<Statement> transform(Assertion a, List<Statement> rest) {
		recurIfNecessary(a, rest)
	}

	def List<Statement> transform(Skip s, List<Statement> rest) {
		recurIfNecessary(s, rest)
	}

	def List<Statement> recurIfNecessary(Statement transformed, List<Statement> rest) {
		def stmts = [transformed]
		if (rest) {
			stmts.addAll(transform(rest.first(), rest.tail()))
		}
		return stmts
	}
}
