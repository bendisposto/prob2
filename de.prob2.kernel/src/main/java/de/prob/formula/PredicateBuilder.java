package de.prob.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;

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
			for (Entry<String, String> entry : map.entrySet()) {
				predicates.add(entry.getKey() + "=" + entry.getValue());
			}
		}
		return this;
	}

	@Override
	public String toString() {
		if (predicates.isEmpty()) {
			return "1=1";
		} else {
			return Joiner.on(" & ").join(predicates);
		}
	}
}
