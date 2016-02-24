package de.prob.model.eventb

/**
 * In the Groovy DSL, formulas are defined by a label and a String formula. This can
 * be represented as a map with one single entry in which the String label is the key
 * and the String formula is the value, i.e. [inv1: "x < 1"].
 * @author joy
 *
 */
class Definition {

	String label
	String formula

	/**
	 * The input map must contain exactly one element. The key in the entry represents the label of the formula
	 * and the value in the entry represents the formula of the element. If incorrectly instantiated,
	 * an {@link IllegalArgumentException} will be thrown.
	 * @param map representing the pair label/formula.
	 * @throws IllegalArgumentException
	 */
	def Definition(LinkedHashMap map) {
		if (map.size() != 1) {
			throw new IllegalArgumentException("Definitions must define only one property")
		}
		def prop = map.collect { k,v -> [k, v]}[0]
		if (!(prop[0] instanceof String && prop[1] instanceof String)) {
			throw new IllegalArgumentException("Labels and formulas for definitions must be strings")
		}
		this.label = prop[0]
		this.formula = prop[1]
	}
}
