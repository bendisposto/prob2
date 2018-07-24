package de.prob.model.eventb;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;

import org.eventb.core.ast.extension.IFormulaExtension;

public class Witness extends AbstractElement {

	private final String name;
	private final EventB predicate;
	private final String comment;

	public Witness(final String name, final String code,
			final Set<IFormulaExtension> typeEnv) {
		this(name, new EventB(code, typeEnv, FormulaExpand.EXPAND), "");
	}

	public Witness(final String name, EventB predicate, String comment) {
		this.name = name;
		this.comment = comment == null ? "" : comment;
		this.predicate = predicate;
	}

	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}

	public IEvalElement getFormula() {
		return this.getPredicate();
	}

	public String getComment() {
		return comment;
	}

}
