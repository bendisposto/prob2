package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.ast.AlgorithmASTVisitor;
import de.prob.model.eventb.algorithm.ast.Assertion;
import de.prob.model.eventb.algorithm.ast.Assignment;
import de.prob.model.eventb.algorithm.ast.Call;
import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Return;
import de.prob.model.eventb.algorithm.ast.Skip;
import de.prob.model.eventb.algorithm.ast.While;
import de.prob.model.representation.ModelElementList;

public class VariantOrdering extends AlgorithmASTVisitor {
	private ModelElementList<While> ordering = new ModelElementList<>();
	
	@Override
	public Object traverse(While s) {
		if (s.getVariant() != null) {
			ordering = ordering.addElement(s);
		}

		return visit(s.getBlock());
	}

	@Override
	public Object visit(While w) {
		return null;
	}

	@Override
	public Object visit(If i) {
		return null;
	}

	@Override
	public Object visit(Assignment a) {
		return null;
	}

	@Override
	public Object visit(Assertion a) {
		return null;
	}

	@Override
	public Object visit(Call a) {
		return null;
	}

	@Override
	public Object visit(Return a) {
		return null;
	}

	@Override
	public Object visit(Skip a) {
		return null;
	}

	public ModelElementList<While> getOrdering() {
		return ordering;
	}

	public void setOrdering(ModelElementList<While> ordering) {
		this.ordering = ordering;
	}
}
