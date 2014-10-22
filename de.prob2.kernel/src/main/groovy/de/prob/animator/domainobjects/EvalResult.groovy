package de.prob.animator.domainobjects;


import org.apache.commons.collections.map.LRUMap

import com.google.common.base.Joiner

import de.prob.parser.BindingGenerator
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm
import de.prob.statespace.StateSpace
import de.prob.unicode.UnicodeTranslator

public class EvalResult implements IEvalResult {

	final static EvalResult TRUE = new EvalResult("TRUE", "TRUE", [:])
	final static EvalResult FALSE = new EvalResult("FALSE", "FALSE", [:])
	final static LRUMap formulaCache = new LRUMap(50)

	final String code;
	final String value;
	final Map<String, String> solutions;

	// These fields are saved in this class to be able to later produce
	// a TranslatedEvalResult from this class. However, they are otherwise
	// not of use to the user.

	public EvalResult(final String code, final String value,
	final Map<String, String> solutions) {
		this.code = code
		this.value = value
		this.solutions = solutions
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		if (solutions.isEmpty()) {
			return value;
		}
		def sols = solutions.collect { "${it.getKey()} = ${it.getValue()}" }

		return value + " (" + UnicodeTranslator.toUnicode(Joiner.on(" & ").join(sols)) + ")";
	}

	/**
	 * This is marked as deprecated because it is intended for use as groovy magic (i.e. it allows
	 * the solution for an EvalResult class to be accessed as a property of the class. If 'x' is a
	 * solution for the result, the solution can be accessed in a groovy environment as res.x)
	 * For use within a Java environment, use method {@link EvalResult#getSolution(String)}
	 *
	 * @param name of solution
	 * @return This will search for a solution with a given name an return it
	 */
	@Deprecated
	def getProperty(String name) {
		if(solutions.containsKey(name)) {
			return getSolution(name)
		}
		return getMetaClass().getProperty(this, name)
	}

	/**
	 * Get the String representation of the value of the solution with the specified name
	 * @param name of solution
	 * @return String representation of solution, or <code>null</code> if no solution with that name exists
	 */
	def String getSolution(String name) {
		return solutions[name]
	}


	def TranslatedEvalResult translate(StateSpace s) {
		// TODO: Reimplement this.
		throw new IllegalArgumentException("cannot translate EvalResults right now.")

		/*	if(valueSource == null) {
		 throw new RuntimeException("Translation is not supported for this result");
		 }
		 def cmd = new ProcessPrologValuesCommand(code, valueSource, solutionsSource);
		 s.execute(cmd)
		 return cmd.getResult()*/
	}



	/**
	 * Translates the results from ProB into an IEvalResult. This is intended
	 * mainly for internal use, for developers who are writing commands and want
	 * to translate them into an {@link IEvalResult}.
	 *
	 * @param pt PrologTerm
	 * @return IEvalResult translation of pt
	 */
	def static IEvalResult getEvalResult(PrologTerm pt) {
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
		} else {
			/*
			 * If the formula in question was a predicate, the result term will have the following form:
			 * result(Value,Solutions,Code) where Value is 'TRUE','POSSIBLY TRUE', or 'FALSE'
			 * Solutions is then a list of triples bind(Name,Solution,PPSol) where Name is the name
			 * of the free variable calculated by ProB, Solution is the Prolog representation of the
			 * solution, and PPSol is the String pretty print of the solution calculated by Prolog.
			 *
			 * If the formula in question was an expression, the result term will have the following form:
			 * result(v(SRes,PRes),[],Code) where SRes is the string representation of the result calculated
			 * by ProB and PRes is the Prolog representation of the value.
			 *
			 * From this information, an EvalResult object is created.
			 */

			PrologTerm v = pt.getArgument(1);
			String value = v.getFunctor();
			ListPrologTerm solutionList = BindingGenerator.getList(pt
					.getArgument(2));
			if (value == "TRUE" && solutionList.isEmpty()) {
				return TRUE
			}
			if (value == "FALSE" && solutionList.isEmpty()) {
				return FALSE
			}
			if (value != "TRUE" && value != "FALSE" && formulaCache.containsKey(value)) {
				return formulaCache.get(value)
			}

			String code = pt.getArgument(3).getFunctor();
			if (v instanceof CompoundPrologTerm && v.getArity() == 2) {
				CompoundPrologTerm cpt = BindingGenerator
						.getCompoundTerm(v, 2);
				value = cpt.getArgument(1).getFunctor();
			}
			Map<String, String> solutions = new HashMap<String, String>();


			for (PrologTerm t : solutionList) {
				CompoundPrologTerm cpt = BindingGenerator
						.getCompoundTerm(t, 3);
				solutions.put(
						cpt.getArgument(1).getFunctor(),
						cpt.getArgument(3).getFunctor());
			}

			def res = new EvalResult(code, value, solutions);
			if (value != "TRUE" && value != "FALSE") {
				formulaCache.put(value, res)
			}
			return res
		}
	}
}
