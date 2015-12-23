package de.prob.model.eventb.algorithm.ast


abstract class AlgorithmASTVisitor {

	public visit(Block block) {
		block.statements.each { traverse(it) }
	}

	def  visit(While w) {}
	def  visit(If i) {}
	def  visit(Assignment a) {}
	def  visit(Assertion a) {}
	def  visit(Call a) {}
	def  visit(Return a) {}
	def  visit(Skip a) {}

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
