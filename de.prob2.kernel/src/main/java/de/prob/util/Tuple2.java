package de.prob.util;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Tuple2<S, T> {
	private final S first;
	private final T second;

	public Tuple2(S first, T second) {
		this.first = first;
		this.second = second;
	}

	public S getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}

	@SuppressWarnings("rawtypes")
	@SuppressFBWarnings(value = "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", justification = "We actually want to compare Tuples with Lists that have 2 elements")
	@Override
	public boolean equals(Object that) {
		if (that instanceof Tuple2<?, ?>) {
			return this.first.equals(((Tuple2) that).getFirst())
					&& this.second.equals(((Tuple2) that).getSecond());
		}
		if (that instanceof List<?>) {
			List<?> list = (List<?>) that;
			if (list.size() == 2) {
				Object first = list.get(0);
				Object second = list.get(1);
				return this.first.equals(first) && this.second.equals(second);
			}
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(getFirst());
		hcb.append(getSecond());
		return hcb.toHashCode();
	}
	
	
}
