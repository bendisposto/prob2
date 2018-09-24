package de.prob.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PredicateBuilder {

	private final List<String> predicates = new ArrayList<>();

	public PredicateBuilder() {
		//
	}

	public PredicateBuilder addList(List<String> predicates) {
		if (predicates != null) {
			this.predicates.addAll(predicates);
		}

		return this;
	}

	public PredicateBuilder addMap(Map<String, String> map) {
		if (map != null) {
			map.forEach((k, v) -> predicates.add(k + '=' + v));
		}
		return this;
	}

	@Override
	public String toString() {
		if (predicates.isEmpty()) {
			return "1=1";
		} else {
			return String.join(" & ", predicates);
		}
	}
}
