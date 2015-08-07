package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class DataTypeConstructor extends AbstractElement {

	private final String identifierString;
	private final EventB identifier;

	public DataTypeConstructor(final String identifier) {
		identifierString = identifier;
		this.identifier = new EventB(identifier);
	}

	private DataTypeConstructor(final String identifier, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		identifierString = identifier;
		this.identifier = new EventB(identifier);
	}

	public DataTypeConstructor addDestructors(
			final ModelElementList<DataTypeDestructor> destructors) {
		return new DataTypeConstructor(identifierString, assoc(DataTypeDestructor.class, destructors));
	}

	public EventB getIdentifier() {
		return identifier;
	}

	public ModelElementList<DataTypeDestructor> getDestructors() {
		return getChildrenOfType(DataTypeDestructor.class);
	}

	@Override
	public String toString() {
		return identifierString;
	}

	public String getUnicode() {
		return identifierString;
	}

	@Override
	public int hashCode() {
		return identifierString.hashCode() + getDestructors().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof DataTypeConstructor) {
			return identifierString.equals(((DataTypeConstructor) obj)
					.toString())
					&& getDestructors().equals(
							((DataTypeConstructor) obj).getDestructors());
		}
		return false;
	}

	public void parseElements(final Set<IFormulaExtension> typeEnv) {
		for (DataTypeDestructor dest : getDestructors()) {
			dest.parseElements(typeEnv);
		}
	}
}
