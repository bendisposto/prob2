package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;

public class Witness extends AbstractElement {

	private final String name;
	private final EventB predicate;
	private final Event parentEvent;

	public Witness(final Event parentEvent, final String name,
			final String code, final Set<IFormulaExtension> typeEnv) {
		this.parentEvent = parentEvent;
		this.name = name;
		predicate = new EventB(code, typeEnv);
	}

	public Event getParentEvent() {
		return parentEvent;
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

}
