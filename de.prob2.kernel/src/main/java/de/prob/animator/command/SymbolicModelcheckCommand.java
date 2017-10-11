package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class SymbolicModelcheckCommand extends AbstractCommand {
	
	public static enum Algorithm {
		BMC("bmc"),
		KINDUCTION("kinduction"),
		TINDUCTION("tinduction"),
		IC3("ic3");
		
		private String prologName;
		
		private Algorithm(final String prologName) {
			this.prologName = prologName;
		}
		
		public String getPrologName() {
			return prologName;
		}
	}
	
	public static enum ResultType {
		SUCCESSFUL, INTERRUPTED, COUNTER_EXAMPLE, TIMEOUT;
	};
	
	private static final String COMMAND_NAME = "symbolic_model_check";
	private static final String RESULT_VARIABLE = "R";
	
	private ResultType result;
	
	private Algorithm algorithm;
	
	public SymbolicModelcheckCommand(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		pto.printAtom(algorithm.getPrologName());
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		if(resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
		} else if(resultTerm.hasFunctor("counterexample_found", 0)) {
			result = ResultType.COUNTER_EXAMPLE;
		} else if(resultTerm.hasFunctor("property_holds", 0)) {
			result = ResultType.SUCCESSFUL;
		} else if(resultTerm.hasFunctor("solver_and_provers_too_weak", 0)) {
			result = ResultType.TIMEOUT;
		}
	}
	
	public ResultType getResult() {
		return result;
	}
	
}
