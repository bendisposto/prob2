package de.prob.model.eventb.theory;

import java.util.List;

import com.google.common.base.Objects;

class DataTypeExtension {

	private final String unicodeDef;
	private final List<DataTypeConstructor> constructors;

	public DataTypeExtension(String identifierString,
			final List<DataTypeConstructor> constructors) {
		unicodeDef = identifierString;
		this.constructors = constructors;
	}

	public String getTypeName() {
		return unicodeDef;
	}

	public String getId() {
		return unicodeDef + " Datatype";
	}

	public List<DataTypeConstructor> getConstructors() {
		return constructors;
	}

	public int hashCode() {
		return Objects.hashCode(getId(), constructors);
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DataTypeExtension other = (DataTypeExtension) obj;
		return Objects.equal(constructors, other.getConstructors())
				&& Objects.equal(getId(), other.getId());
	}

}