package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class RecursiveOperatorDefinition extends AbstractElement implements
		IOperatorDefinition {

	private final EventB inductiveArgument;
	private ModelElementList<RecursiveDefinitionCase> cases = new ModelElementList<RecursiveDefinitionCase>();

	public RecursiveOperatorDefinition(final String inductiveArgument,
			final Set<IFormulaExtension> typeEnv) {
		this.inductiveArgument = new EventB(inductiveArgument, typeEnv);
	}

	public void addCases(final ModelElementList<RecursiveDefinitionCase> cases) {
		put(RecursiveDefinitionCase.class, cases);
		this.cases = cases;
	}

	public ModelElementList<RecursiveDefinitionCase> getCases() {
		return cases;
	}

	public EventB getInductiveArgument() {
		return inductiveArgument;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RecursiveOperatorDefinition) {
			return inductiveArgument.equals(((RecursiveOperatorDefinition) obj)
					.getInductiveArgument());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return inductiveArgument.hashCode();
	}

}
