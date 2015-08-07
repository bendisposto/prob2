package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class DataType extends AbstractElement {

	final String identifierString;
	private final EventB identifier;

	public DataType(final String identifier) {
		identifierString = identifier;
		this.identifier = new EventB(identifier);
	}

	private DataType(final String identifier, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		identifierString = identifier;
		this.identifier = new EventB(identifier);
	}

	public DataType set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		return new DataType(identifierString, assoc(clazz, elements));
	}

	public EventB getTypeIdentifier() {
		return identifier;
	}

	public ModelElementList<DataTypeConstructor> getDataTypeConstructors() {
		return getChildrenOfType(DataTypeConstructor.class);
	}

	public ModelElementList<Type> getTypeArguments() {
		return getChildrenOfType(Type.class);
	}

	@Override
	public String toString() {
		return identifierString;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof DataType) {
			return identifierString.equals(((DataType) obj).toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return identifierString.hashCode();
	}

	public void parseElements(final Set<IFormulaExtension> typeEnv) {
		for (DataTypeConstructor cons : getDataTypeConstructors()) {
			cons.parseElements(typeEnv);
		}
	}

	public Set<IFormulaExtension> getFormulaExtensions(final FormulaFactory ff) {
		IDatatypeBuilder builder = ff.makeDatatypeBuilder(identifierString);
		for (DataTypeConstructor c : getDataTypeConstructors()) {
			builder.addConstructor(c.getUnicode());
		}

		IDatatype datatype = builder.finalizeDatatype();
		return datatype.getExtensions();

	}
}
