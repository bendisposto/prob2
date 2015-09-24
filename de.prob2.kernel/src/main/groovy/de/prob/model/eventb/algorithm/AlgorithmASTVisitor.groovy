package de.prob.model.eventb.algorithm

abstract class AlgorithmASTVisitor {

	public visit(Block block) {
		block.statements.each { traverse(it) }
	}

	public abstract visit(While w);
	public abstract visit(If i);
	public abstract visit(Assignments a);
	public abstract visit(Assertion a);

	public traverse(While s) {
		visit(s)
		visit(s.block)
	}

	public traverse(If i) {
		visit(i)
		visit(i.Then)
		visit(i.Else)
	}

	public traverse(Assignments a) {
		visit(a)
	}

	public traverse(Assertion a) {
		visit(a)
	}
}
