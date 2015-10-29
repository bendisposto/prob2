package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class AssignmentCombiner implements IAlgorithmASTTransformer {

	@Override
	public Block transform(Block algorithm) {
		if (algorithm.statements.isEmpty()) {
			return algorithm
		}
		List<Statement> stmts = mergeAssignments(algorithm.statements.first(), algorithm.statements.tail())
		new Block(stmts, algorithm.typeEnvironment)
	}

	def List<Statement> mergeAssignments(While w, List<Statement> stmts) {
		While newWhile = w.updateBlock(transform(w.block))
		recurIfNecessary(newWhile, stmts)
	}

	def List<Statement> mergeAssignments(If i, List<Statement> stmts) {
		If newIf = i.newIf(transform(i.Then), transform(i.Else))
		recurIfNecessary()
	}

	def List<Statement> recurIfNecessary(Statement transformedStmt, List<Statement> nextS) {
		def list = [transformedStmt]
		if (nextS) {
			list.addAll(mergeAssignments(nextS.head(), nextS.tail()))
		}
		list
	}
}
