package de.prob.model.eventb.theory;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class DataTypeConstructor extends AbstractElement {

	private final EventB identifier;
	private final List<DataTypeDestructor> destructors = new ModelElementList<DataTypeDestructor>();

	DataTypeConstructor(final String identifier,
			final Set<IFormulaExtension> typeEnv) {
		this.identifier = new EventB(identifier, typeEnv);
	}

	public void addDestructors(final List<DataTypeDestructor> destructors) {
		put(DataTypeDestructor.class, destructors);
		this.destructors.addAll(destructors);
	}

	public EventB getIdentifier() {
		return identifier;
	}

	public List<DataTypeDestructor> getDestructors() {
		return destructors;
	}

	@Override
	public String toString() {
		return identifier.getCode();
	}

	@Override
	public int hashCode() {
		return identifier.hashCode() + getDestructors().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof DataTypeConstructor) {
			return identifier.equals(((DataTypeConstructor) obj)
					.getIdentifier())
					&& getDestructors().equals(
							((DataTypeConstructor) obj).getDestructors());
		}
		return false;
	}
}
