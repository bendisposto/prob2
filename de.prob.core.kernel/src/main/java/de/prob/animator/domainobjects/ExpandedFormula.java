package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

public class ExpandedFormula {

	private String name;
	private Object value;
	private String id;
	private List<ExpandedFormula> children;
	private final Map<String, Object> fields = new HashMap<String, Object>();

	public ExpandedFormula(final CompoundPrologTerm cpt) {
		init(cpt);
	}

	public void init(final CompoundPrologTerm cpt) {
		name = cpt.getArgument(1).getFunctor();
		fields.put("name", escapeUnicode(name));

		// Value
		PrologTerm v = cpt.getArgument(2);
		value = getValue(v);
		fields.put("value",
				value instanceof String ? escapeUnicode((String) value) : value);
		// Children
		id = cpt.getArgument(3).getFunctor();
		fields.put("id", id);

		ListPrologTerm list = BindingGenerator.getList(cpt.getArgument(4));
		if (!list.isEmpty()) {
			children = new ArrayList<ExpandedFormula>();
			List<Object> childrenFields = new ArrayList<Object>();
			for (PrologTerm prologTerm : list) {
				ExpandedFormula expandedFormula = new ExpandedFormula(
						BindingGenerator.getCompoundTerm(prologTerm, 4));
				children.add(expandedFormula);
				childrenFields.add(expandedFormula.getFields());
			}
			fields.put("children", childrenFields);
		}
	}

	private String escapeUnicode(final String data) {
		return StringEscapeUtils.escapeJava(data);
	}

	private Object getValue(final PrologTerm v) {
		if (v.getFunctor().equals("p")) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
			if (cpt.getArgument(1).getFunctor().equals("true")) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else if (v.getFunctor().equals("v")) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(v, 1);
			return UnicodeTranslator.toUnicode(cpt.getArgument(1).getFunctor());
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

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return fields.toString();
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public void toggle() {
		if (fields.containsKey("children")) {
			Object _children = fields.get("children");
			fields.put("_children", _children);
			fields.remove("children");
		} else {
			Object children = fields.get("_children");
			fields.put("children", children);
			fields.remove("_children");
		}
	}

	public void collapseNodes(final Set<String> collapsedNodes) {
		if (collapsedNodes.contains(id)) {
			toggle();
			collapsedNodes.remove(id);
		}
		if (children == null) {
			return;
		}
		if (collapsedNodes.isEmpty()) {
			return;
		}
		for (ExpandedFormula expandedFormula : children) {
			expandedFormula.collapseNodes(collapsedNodes);
		}
	}

}
