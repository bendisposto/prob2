package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class RecursiveOperatorDefinition extends AbstractElement implements
		IOperatorDefinition {

	private final EventB inductiveArgument;

	public RecursiveOperatorDefinition(final String inductiveArgument) {
		this.inductiveArgument = new EventB(inductiveArgument);
	}

	public void addCases(final List<RecursiveDefinitionCase> cases) {
		put(RecursiveDefinitionCase.class, cases);
	}

	public List<RecursiveDefinitionCase> getCases() {
		return new ModelElementList<RecursiveDefinitionCase>(
				getChildrenOfType(RecursiveDefinitionCase.class));
	}

	public EventB getInductiveArgument() {
		return inductiveArgument;
	}

}
