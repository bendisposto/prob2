package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.ast.*;
import de.prob.model.representation.ModelElementList;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class VariantOrdering extends AlgorithmASTVisitor {
	@Override
	public Object traverse(While s) {
		if (DefaultGroovyMethods.asBoolean(s.getVariant())) {
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

	private ModelElementList<While> ordering = new ModelElementList<While>();
}
