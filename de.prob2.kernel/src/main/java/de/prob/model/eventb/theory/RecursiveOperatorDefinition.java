package de.prob.model.eventb.theory;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

import org.eventb.core.ast.extension.IFormulaExtension;

public class RecursiveOperatorDefinition extends AbstractElement implements
IOperatorDefinition {

	private final EventB inductiveArgument;
	private final ModelElementList<RecursiveDefinitionCase> cases;

	public RecursiveOperatorDefinition(final String inductiveArgument,
			final Set<IFormulaExtension> typeEnv) {
		this.inductiveArgument = new EventB(inductiveArgument, typeEnv);
		this.cases = new ModelElementList<>();
	}

	private RecursiveOperatorDefinition(final EventB inductiveArgument, ModelElementList<RecursiveDefinitionCase> cases) {
		this.inductiveArgument = inductiveArgument;
		this.cases = cases;
	}

	public RecursiveOperatorDefinition addCases(final ModelElementList<RecursiveDefinitionCase> cases) {
		return new RecursiveOperatorDefinition(inductiveArgument, cases);
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
