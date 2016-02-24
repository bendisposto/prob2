package de.prob.util;

import java.util.List;

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
}
