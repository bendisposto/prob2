package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets a list of redundant invariants and timeouts from ProB
 * 
 * @author joy
 * 
 */
public class GetRedundantInvariantsCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "prob2_redundant_invariants";
	private static final String REDUNDANT_INVS = "Invariants";
	private static final String TIMEOUT_VAR = "Timeouts";

	private List<String> redundant = Collections.emptyList();
	private boolean timeout;

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		redundant = PrologTerm.atomicStrings((ListPrologTerm) bindings.get(REDUNDANT_INVS));
		timeout = "true".equals(bindings.get(TIMEOUT_VAR).getFunctor());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(REDUNDANT_INVS).printVariable(TIMEOUT_VAR).closeTerm();
	}

	public List<String> getRedundantInvariants() {
		return redundant;
	}

	public boolean isTimeout() {
		return timeout;
	}
}
