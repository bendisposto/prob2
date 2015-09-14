package de.prob.model.eventb.theory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.model.representation.AbstractElement;
import de.prob.util.Tuple2;

public class DataType extends AbstractElement {

	final String identifierString;
	private final Map<String, List<Tuple2<String, String>>> constructors;
	private final List<String> typeArguments;

	public DataType(final String identifier,
			Map<String, List<Tuple2<String, String>>> constructors,
			List<String> types) {
		identifierString = identifier;
		this.constructors = constructors;
		this.typeArguments = types;
	}

	public String getTypeIdentifier() {
		return identifierString;
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

	public Map<String, List<Tuple2<String, String>>> getConstructors() {
		return constructors;
	}

	public Set<IFormulaExtension> getFormulaExtensions(final FormulaFactory ff) {
		List<GivenType> types = new ArrayList<GivenType>();
		for (String type : typeArguments) {
			IParseResult parseType = ff.parseType(type);
			if (parseType.getParsedType() instanceof GivenType) {
				types.add((GivenType) parseType.getParsedType());
			}
		}
		assert types.size() == typeArguments.size();
		IDatatypeBuilder builder = ff.makeDatatypeBuilder(identifierString,
				types.toArray(new GivenType[typeArguments.size()]));
		for (Entry<String, List<Tuple2<String, String>>> entry : constructors
				.entrySet()) {
			IConstructorBuilder consBuilder = builder.addConstructor(entry
					.getKey());
			for (Tuple2<String, String> destructor : entry.getValue()) {
				IParseResult parseType = ff.parseType(destructor.getSecond());
				consBuilder.addArgument(destructor.getFirst(),
						parseType.getParsedType());
			}
		}

		IDatatype datatype = builder.finalizeDatatype();
		Set<IFormulaExtension> extensions = datatype.getExtensions();
		return extensions;
	}

	public List<String> getTypeArguments() {
		return typeArguments;
	}
}
