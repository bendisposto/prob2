/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import de.prob.check.StateSpaceStats;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComputeStateSpaceStatsCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "compute_efficient_statespace_stats";
	private StateSpaceStats coverageResult;
	Logger logger = LoggerFactory.getLogger(ComputeStateSpaceStatsCommand.class);

	public StateSpaceStats getResult() {
		return coverageResult;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		int nrNodes = ((IntegerPrologTerm) bindings
				.get("NrNodes")).getValue().intValue();
		int nrTrans = ((IntegerPrologTerm) bindings
				.get("NrTrans")).getValue().intValue();
		int nrProcessed = ((IntegerPrologTerm) bindings
				.get("NrProcessed")).getValue().intValue();

		coverageResult = new StateSpaceStats(nrNodes, nrTrans, nrProcessed);

	}

	// NrNodes, NrTrans, NrProcessed
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable("NrNodes")
				.printVariable("NrTrans").printVariable("NrProcessed")
				.closeTerm();
	}

}
