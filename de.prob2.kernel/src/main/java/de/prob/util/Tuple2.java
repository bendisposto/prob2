package de.prob.util;

import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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

	@SuppressFBWarnings(value = "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", justification = "We actually want to compare Tuples with Lists that have 2 elements")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		// FIXME Is this even a good idea? This makes equals not commutative (aTuple.equals(aList) != aList.equals(aTuple)).
		// Note: If this branch is removed, please also remove the @SuppressFBWarnings annotation above.
		if (obj instanceof List<?>) {
			final List<?> list = (List<?>)obj;
			if (list.size() == 2) {
				return Objects.equals(this.getFirst(), list.get(0)) && Objects.equals(this.getSecond(), list.get(1));
			}
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Tuple2<?, ?> other = (Tuple2<?, ?>)obj;
		return Objects.equals(this.getFirst(), other.getFirst())
				&& Objects.equals(this.getSecond(), other.getSecond());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getFirst(), this.getSecond());
	}
}
