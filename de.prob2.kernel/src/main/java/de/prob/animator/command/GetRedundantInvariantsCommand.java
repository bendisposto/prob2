package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets a list of the errors from ProB
 * 
 * @author joy
 * 
 */
public class GetRedundantInvariantsCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "prob2_redundant_invariants";
	public static final String REDUNDANT_INVS = "Invariants";
	public static final String TIMEOUT = "Timeouts";

	private List<String> redundant = Collections.emptyList();
	private List<String> timeout = Collections.emptyList();

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		redundant = PrologTerm.atomicStrings((ListPrologTerm) bindings.get(REDUNDANT_INVS));
		timeout = PrologTerm.atomicStrings((ListPrologTerm) bindings.get(TIMEOUT));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(REDUNDANT_INVS).printVariable(TIMEOUT).closeTerm();
	}

	public List<String> getRedundantInvariants() {
		return redundant;
	}

	public List<String> getRedundantInvariantsTimeout() {
		return timeout;
	}

}