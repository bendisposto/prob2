package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Variable;

public class MetaVariable extends Variable {

	private final EventB type;

	public MetaVariable(final String identifier, final String type,
			final Set<IFormulaExtension> typeEnv) {
		super(new EventB(identifier, typeEnv));
		this.type = new EventB(type, typeEnv);
	}

	public EventB getType() {
		return type;
	}

}
