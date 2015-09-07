package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;

public class Witness extends AbstractElement {

	private final String name;
	private final EventB predicate;
	private String comment;

	public Witness(final String name, final String code,
			final Set<IFormulaExtension> typeEnv) {
		this(name, code, typeEnv, "");
	}

	public Witness(final String name, final String code,
			final Set<IFormulaExtension> typeEnv, String comment) {
		this.name = name;
		this.comment = comment;
		predicate = new EventB(code, typeEnv);
	}

	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}

	public IEvalElement getFormula() {
		return predicate;
	}

	public String getComment() {
		return comment;
	}

}
