package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Creates an evaluation store based on a state from the state space.
 * 
 * An evaluation store can be used to evaluate formulas on it and to add new
 * identifiers with new values to create a new evaluation store.
 * 
 * @author plagge
 */
public class EvalstoreCreateByStateCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "es_copy_from_statespace";
	private static final String STORE_ID_VAR = "ID";
	private final String stateId;
	private long evalstoreId;

	public EvalstoreCreateByStateCommand(final String stateId) {
		this.stateId = stateId;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(stateId);
		pto.printVariable(STORE_ID_VAR);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// TODO[DP, 22.01.2013]: Handle errors?
		final IntegerPrologTerm result = (IntegerPrologTerm) bindings
				.get(STORE_ID_VAR);
		evalstoreId = result.getValue().longValue();
	}

	public long getEvalstoreId() {
		return evalstoreId;
	}

}
