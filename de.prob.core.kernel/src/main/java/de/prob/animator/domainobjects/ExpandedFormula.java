package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ExpandedFormula {

	private String name;
	private Object value;
	// private String fId;
	private List<ExpandedFormula> children;

	public ExpandedFormula(final CompoundPrologTerm cpt) {
		init(cpt);
	}

	public void init(final CompoundPrologTerm cpt) {
		name = cpt.getArgument(1).getFunctor();
		// Value
		PrologTerm v = cpt.getArgument(2);
		value = getValue(v);
		// Children
		// fId = cpt.getArgument(3).getFunctor();

		ListPrologTerm list = BindingGenerator.getList(cpt.getArgument(4));
		if (!list.isEmpty()) {
			children = new ArrayList<ExpandedFormula>();
			for (PrologTerm prologTerm : list) {
				children.add(new ExpandedFormula(BindingGenerator
						.getCompoundTerm(prologTerm, 4)));
			}
		}
	}

	private Object getValue(final PrologTerm v) {
		if (v.getFunctor().equals("p")) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
			if (cpt.getArgument(1).getFunctor().equals("true"))
				return Boolean.TRUE;
			else
				return Boolean.FALSE;
		} else if (v.getFunctor().equals("v")) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
			return cpt.getArgument(1).getFunctor();
		}
		v.getFunctor();
		return null;
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

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
