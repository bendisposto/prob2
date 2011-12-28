package de.prob.model.values;


public class ValueFactory {

	static AbstractValue createAtom(String value) {
		return new AtomValue(value);
	}

	static AbstractValue createPair(AbstractValue left, AbstractValue right) {
		return new PairValue(left, right);
	}

	static AbstractValue createList(AbstractValue... values) {
		return new ListValue(values);
	}

}
