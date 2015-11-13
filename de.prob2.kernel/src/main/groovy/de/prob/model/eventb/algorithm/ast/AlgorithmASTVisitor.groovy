package de.prob.model.eventb.algorithm.ast


abstract class AlgorithmASTVisitor {

	public visit(Block block) {
		block.statements.each { traverse(it) }
	}

	public abstract visit(While w);
	public abstract visit(If i);
	public abstract visit(Assignment a);
	public abstract visit(Assertion a);
	public abstract visit(Call a);
	public abstract visit(Return a);
	public abstract visit(Skip a);

	public traverse(While s) {
		visit(s)
		visit(s.block)
	}

	public traverse(If i) {
		visit(i)
		visit(i.Then)
		visit(i.Else)
	}

	public traverse(Assignment a) {
		visit(a)
	}

	public traverse(Assertion a) {
		visit(a)
	}

	public traverse(Call a) {
		visit(a)
	}

	public traverse(Return a) {
		visit(a)
	}

	public traverse(Skip a) {
		visit(a)
	}
}
