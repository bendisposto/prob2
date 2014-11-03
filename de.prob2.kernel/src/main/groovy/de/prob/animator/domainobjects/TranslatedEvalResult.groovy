package de.prob.animator.domainobjects

public class TranslatedEvalResult implements IEvalResult {

	def value
	def Map<String,Object> solutions

	def TranslatedEvalResult(value, Map<String,Object> solutions) {
		this.value = value
		this.solutions = solutions
	}

	/**
	 * This method should not be accessed in a Java environment. It has therefore been marked as deprecated.
	 * It is implemented in order to allow for Groovy magic in a Groovy environment (i.e. to a user, the solutions
	 * can be accessed via name on the class, as if the name of the solution were a field in the class. result.x will
	 * attempt to find a solution with name x and return it to the user)
	 * If programming in a Java environment, you can use {@link TranslatedEvalResult#getSolution(String)} for the same effect.
	 *
	 * @param name of solution
	 * @return Object representation of the solution
	 */
	@Deprecated
	def getProperty(String name) {
		if(solutions.containsKey(name)) {
			return getSolution(name)
		}
		return getMetaClass().getProperty(this, name)
	}

	/**
	 * Tries to access a solution with the given name for the result.
	 * @param name of solution
	 * @return Object representation of solution, or <code>null</code> if the solution does not exist
	 */
	def getSolution(String name) {
		return solutions[name]
	}

	def String toString() {
		return value.toString();
	}
}
