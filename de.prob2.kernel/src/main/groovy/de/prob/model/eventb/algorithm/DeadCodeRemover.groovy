package de.prob.model.eventb.algorithm

class DeadCodeRemover implements IAlgorithmASTTransformer {

	@Override
	public Block transform(Block algorithm) {
		List<Statement> statements = []
		boolean returned = false
		algorithm.statements.each {
			if (!returned) {
				if (it instanceof While || it instanceof If) {
					statements << transform(it)
				} else {
					statements << it
				}
			}
			returned = returned ?: it instanceof Return
		}

		return new Block(statements, algorithm.typeEnvironment)
	}

	public While transform(While w) {
		return new While(w.condition, w.notCondition, w.variant, w.invariant, transform(w.block), w.typeEnvironment)
	}

	public If transform(If i) {
		return new If(i.condition, i.elseCondition, transform(i.Then), transform(i.Else), i.typeEnvironment)
	}
}
