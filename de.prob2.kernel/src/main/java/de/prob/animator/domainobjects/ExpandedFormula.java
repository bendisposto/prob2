package de.prob.animator.domainobjects;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

public class ExpandedFormula {
	private final String name;
	private final Object value;
	private final String id;
	private List<ExpandedFormula> children;

	public ExpandedFormula(final CompoundPrologTerm cpt) {
		name = cpt.getArgument(1).getFunctor();
		value = getValue(cpt.getArgument(2));
		id = cpt.getArgument(3).getFunctor();
		children = BindingGenerator.getList(cpt.getArgument(4)).stream()
			.map(pt -> new ExpandedFormula(BindingGenerator.getCompoundTerm(pt, 4)))
			.collect(Collectors.toList());
	}

	private static Object getValue(final PrologTerm v) {
		String functor = v.getFunctor();
		CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
		switch (functor) {
			case "p":
				return "true".equals(cpt.getArgument(1).getFunctor());
			case "v":
				return UnicodeTranslator.toUnicode(cpt.getArgument(1).getFunctor());
			case "e":
				return cpt.getArgument(1).getFunctor();
			default:
				throw new IllegalArgumentException("Received unexpected result from Prolog. "
						+ "Expected is either p, v or e as a functor, but Prolog returned " + functor);
		}
	}

	public String getLabel() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public List<ExpandedFormula> getChildren() {
		return children;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("name", this.getLabel())
			.add("value", this.getValue())
			.add("id", this.getId())
			.add("children", this.getChildren())
			.toString();
	}
}
