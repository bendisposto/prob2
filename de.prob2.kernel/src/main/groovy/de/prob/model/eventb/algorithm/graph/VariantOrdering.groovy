package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.AlgorithmASTVisitor
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.representation.ModelElementList

class VariantOrdering extends AlgorithmASTVisitor {

	def ModelElementList<While> ordering = new ModelElementList<While>()

	@Override
	public Object traverse(While s) {
		visit(s.block)
		if (s.variant) {
			ordering = ordering.addElement(s)
		}
	}

	@Override
	public Object visit(While w) {
	}

	@Override
	public Object visit(If i) {
	}

	@Override
	public Object visit(Assignment a) {
	}

	@Override
	public Object visit(Assertion a) {
	}

	@Override
	public Object visit(Call a) {
	}

	@Override
	public Object visit(Return a) {
	}

	@Override
	public Object visit(Skip a) {
	}
}
