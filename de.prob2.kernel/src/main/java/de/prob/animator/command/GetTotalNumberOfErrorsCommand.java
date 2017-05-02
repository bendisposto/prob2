package de.prob.animator.command;

import java.math.BigInteger;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets the total number of errors in the prolog error manager since start of
 * the probcli process. Cannot be reseted to zero. This is used for validation
 * of ProB to ensure that no error slips through the cracks.
 * 
 * 
 */
public class GetTotalNumberOfErrorsCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_prob_total_number_of_errors";
	public static final String ERRORS_VARIABLE = "NumberOfErrors";
	private BigInteger value;

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		IntegerPrologTerm prologTerm = (IntegerPrologTerm) bindings.get(ERRORS_VARIABLE);
		value = prologTerm.getValue();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(ERRORS_VARIABLE).closeTerm();
	}

	public BigInteger getTotalNumberOfErrors() {
		return value;
	}

}