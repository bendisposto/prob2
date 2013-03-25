package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ExpandedFormula {

	private String label;
	private Object value;
	private List<ExpandedFormula> children = new ArrayList<ExpandedFormula>();
	private final Map<String,Object> objects = new HashMap<String, Object>();

	public ExpandedFormula(CompoundPrologTerm cpt) {
		init(cpt);
	}

	public void init(CompoundPrologTerm cpt) {
		label = cpt.getArgument(1).getFunctor();
		// Value
		PrologTerm v = cpt.getArgument(2);
		value = getValue(v);
		// Children
		ListPrologTerm list = BindingGenerator.getList(cpt.getArgument(3));
		for (PrologTerm prologTerm : list) {
			children.add(new ExpandedFormula(BindingGenerator.getCompoundTerm(prologTerm, 3)));
		}
		
		objects.put("label", label);
		objects.put("value", value);
		objects.put("children", children);
		
		
	}

	private Object getValue(PrologTerm v) {
		if(v.getFunctor().equals("p")) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
			if(cpt.getArgument(1).getFunctor().equals("true")) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else if(v.getFunctor().equals("v")) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
			return cpt.getArgument(1).getFunctor();
		}
		v.getFunctor();
		return null;
	}
	
	public String getLabel() {
		return label;
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
		return gson.toJson(objects);
	}
	
}
