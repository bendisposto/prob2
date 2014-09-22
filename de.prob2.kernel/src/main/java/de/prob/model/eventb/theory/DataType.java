package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class DataType extends AbstractElement {

	final String identifierString;
	private final EventB identifier;
	private ModelElementList<Type> typeArguments = new ModelElementList<Type>();
	private ModelElementList<DataTypeConstructor> dataTypeConstructors = new ModelElementList<DataTypeConstructor>();

	public DataType(final String identifier) {
		identifierString = identifier;
		this.identifier = new EventB(identifier);
	}

	public void addTypeArguments(final ModelElementList<Type> arguments) {
		put(Type.class, arguments);
		typeArguments = arguments;
	}

	public void addConstructors(
			final ModelElementList<DataTypeConstructor> constructors) {
		put(DataTypeConstructor.class, constructors);
		dataTypeConstructors = constructors;
	}

	public EventB getTypeIdentifier() {
		return identifier;
	}

	public ModelElementList<DataTypeConstructor> getDataTypeConstructors() {
		return dataTypeConstructors;
	}

	public ModelElementList<Type> getTypeArguments() {
		return typeArguments;
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
		for (DataTypeConstructor cons : dataTypeConstructors) {
			cons.parseElements(typeEnv);
		}
	}

	public Set<IFormulaExtension> getFormulaExtensions(final FormulaFactory ff) {
		IDatatypeBuilder builder = ff.makeDatatypeBuilder(identifierString);
		for (DataTypeConstructor c : dataTypeConstructors) {
			builder.addConstructor(c.getUnicode());
		}

		IDatatype datatype = builder.finalizeDatatype();
		return datatype.getExtensions();

	}
}
