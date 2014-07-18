package de.prob.animator.domainobjects;

import com.google.common.base.Joiner

import de.prob.animator.command.ProcessPrologValuesCommand
import de.prob.parser.BindingGenerator
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm
import de.prob.statespace.StateSpace
import de.prob.unicode.UnicodeTranslator

public class EvalResult implements IEvalResult {

	final String code;
	final String value;
	final Map<String, String> solutions;

	// These fields are saved in this class to be able to later produce
	// a TranslatedEvalResult from this class. However, they are otherwise
	// not of use to the user.
	private final PrologTerm valueSource;
	private final Map<String, PrologTerm> solutionsSource;


	public EvalResult(final String code, final String value, final PrologTerm valueSource,
	final Map<String, String> solutions,
	final Map<String, PrologTerm> solutionsSource) {
		this.code = code
		this.value = value
		this.valueSource = valueSource
		this.solutions = solutions
		this.solutionsSource = solutionsSource
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
		if(valueSource == null) {
			throw new RuntimeException("Translation is not supported for this result");
		}
		def cmd = new ProcessPrologValuesCommand(code, valueSource, solutionsSource);
		s.execute(cmd)
		return cmd.getResult()
	}

	def static IEvalResult getEvalResult(PrologTerm pt) {
		if (pt instanceof ListPrologTerm) {
			ListPrologTerm listP = (ListPrologTerm) pt
			def list = []

			String code = listP.get(0).getFunctor();

			for (int i = 1; i < listP.size(); i++) {
				list.add(listP.get(i).getArgument(1).getFunctor());
			}

			return new ComputationNotCompletedResult(code, Joiner.on(",").join(list))
		} else {
			PrologTerm v = pt.getArgument(1);
			String value = v.getFunctor();
			def vSource = v;
			if (v instanceof CompoundPrologTerm && v.getArity() == 2) {
				CompoundPrologTerm cpt = BindingGenerator
						.getCompoundTerm(v, 2);
				value = cpt.getArgument(1).getFunctor();
				vSource = cpt.getArgument(2)
			}
			Map<String, String> solutions = new HashMap<String, String>();
			Map<String, PrologTerm> solutionsSource = new HashMap<String, PrologTerm>();

			ListPrologTerm list = BindingGenerator.getList(pt
					.getArgument(2));
			for (PrologTerm t : list) {
				CompoundPrologTerm cpt = BindingGenerator
						.getCompoundTerm(t, 3);
				solutions.put(
						cpt.getArgument(1).getFunctor(),
						cpt.getArgument(3).getFunctor());
				solutionsSource.put(cpt.getArgument(1).getFunctor(),
						cpt.getArgument(2));
			}
			String code = pt.getArgument(3).getFunctor();
			return new EvalResult(code, value, vSource, solutions, solutionsSource);
		}
	}
}
