package de.prob.animator.domainobjects

import com.google.common.base.Joiner

import de.prob.parser.BindingGenerator
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm

public class TranslatedEvalResult extends AbstractEvalResult {

	def value
	def Map<String,Object> solutions

	def TranslatedEvalResult(value, Map<String,Object> solutions) {
		super();
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

	def static AbstractEvalResult getResult(PrologTerm pt) {
		if (pt instanceof ListPrologTerm) {
			/*
			 * If the evaluation was not successful, the result should be a
			 * Prolog list with the code on the first index and a list of errors
			 * This results therefore in a ComputationNotCompleted command
			 */
			ListPrologTerm listP = (ListPrologTerm) pt
			def list = []

			String code = listP.get(0).getFunctor();

			for (int i = 1; i < listP.size(); i++) {
				list.add(listP.get(i).getArgument(1).getFunctor());
			}

			return new ComputationNotCompletedResult(code, Joiner.on(",").join(list))
		} else if(pt.getFunctor() == "result"){
			PrologTerm v = pt.getArgument(1);
			ValueTranslator translator = new ValueTranslator();
			Object vobj = translator.toGroovy(v);

			ListPrologTerm list = BindingGenerator.getList(pt
					.getArgument(2));
			Map<String, Object> solutions = Collections.emptyMap();
			if (!list.isEmpty()) {
				solutions = new HashMap<String, Object>();
			}
			for (PrologTerm pt2 : list) {
				CompoundPrologTerm sol = BindingGenerator.getCompoundTerm(pt2, 2);
				solutions.put(sol.getArgument(1).getFunctor(),
						translator.toGroovy(sol.getArgument(2)));
			}
			return new TranslatedEvalResult(vobj, solutions);
		} else if (pt.getFunctor() == "errors" && pt.getArgument(1).getFunctor() == "NOT-WELL-DEFINED") {
			ListPrologTerm arg2 = BindingGenerator.getList(pt.getArgument(2))
			return new WDError(arg2.collect { it.getFunctor()})
		} else if (pt.getFunctor() == "errors" && pt.getArgument(1).getFunctor() == "IDENTIFIER(S) NOT YET INITIALISED") {
			ListPrologTerm arg2 = BindingGenerator.getList(pt.getArgument(2))
			return new IdentifierNotInitialised(arg2.collect { it.getFunctor()})
		} else if (pt.getFunctor() == "enum_warning") {
			return new EnumerationWarning()
		}
		throw new IllegalArgumentException("Unknown result type "+pt.toString())
	}
}
