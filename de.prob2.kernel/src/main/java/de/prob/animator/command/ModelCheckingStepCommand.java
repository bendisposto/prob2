/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckErrorUncovered;
import de.prob.check.ModelCheckOk;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.NotYetFinished;
import de.prob.check.StateSpaceStats;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ModelCheckingStepCommand extends AbstractCommand {

	/**
	 * <p>
	 * The prolog core returns a compound term containing the current stats
	 * about the state space. This term has the arity {@link #STATS_ARITY}.
	 * <p>
	 * 
	 * <p>
	 * The arguments of the term have the following meanings
	 * </p>
	 * <ol>
	 * <li>Total number of nodes in the state space</li>
	 * <li>Total number of transitions in the state space</li>
	 * <li>Number of nodes that have already been processed</li>
	 * <ol>
	 */
	private final static int STATS_ARITY = 3;

	private final int time;
	private final ModelCheckingOptions options;
	private IModelCheckingResult result;
	private final String RESULT = "Result";
	private final String STATS = "Stats";

	Logger logger = LoggerFactory.getLogger(ModelCheckingStepCommand.class);

	private StateSpaceStats stats;

	public ModelCheckingStepCommand(final int time,
			final ModelCheckingOptions options) {
		this.time = time;
		this.options = options;
	}

	public IModelCheckingResult getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm statsTerm = BindingGenerator.getCompoundTerm(
				bindings.get(STATS), STATS_ARITY);
		int numberNodes = BindingGenerator.getInteger(statsTerm.getArgument(1))
				.getValue().intValue();
		int numberTrans = BindingGenerator.getInteger(statsTerm.getArgument(2))
				.getValue().intValue();
		int numberProcessed = BindingGenerator
				.getInteger(statsTerm.getArgument(3)).getValue().intValue();

		stats = new StateSpaceStats(numberNodes, numberTrans, numberProcessed);
		result = extractResult(bindings.get(RESULT));
	}

	private IModelCheckingResult extractResult(final PrologTerm prologTerm) {
		CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(prologTerm,
				prologTerm.getArity());

		String type = cpt.getFunctor();

		if (type.equals("not_yet_finished")) {
			int maxNodesLeft = BindingGenerator.getInteger(cpt.getArgument(1))
					.getValue().intValue();
			return new NotYetFinished("Model checking not completed",
					maxNodesLeft);
		}

		if (type.equals("ok")) {
			return new ModelCheckOk(
					"Model Checking complete. No error nodes found.");
		}

		if (type.equals("full_coverage")) {
			return new ModelCheckOk(
					"Model Checking complete. All operations were covered.");
		}

		if (type.equals("ok_not_all_nodes_considered")) {
			return new ModelCheckOk(
					"Model Checking complete. No error nodes found. Not all nodes were considered.");
		}

		if (type.equals("deadlock")) {
			return new ModelCheckErrorUncovered("Deadlock found.", cpt
					.getArgument(1).getFunctor());
		}

		if (type.equals("invariant_violation")) {
			return new ModelCheckErrorUncovered("Invariant violation found.",
					cpt.getArgument(1).getFunctor());
		}

		if (type.equals("assertion_violation")) {
			return new ModelCheckErrorUncovered("Assertion violation found.",
					cpt.getArgument(1).getFunctor());
		}

		if (type.equals("state_error")) {
			return new ModelCheckErrorUncovered("A state error occured.", cpt
					.getArgument(1).getFunctor());
		}

		if (type.equals("goal_found")) {
			return new ModelCheckErrorUncovered("Goal found", cpt
					.getArgument(1).getFunctor());
		}

		if (type.equals("well_definedness_error")) {
			return new ModelCheckErrorUncovered(
					"A well definedness error occured.", cpt.getArgument(1)
							.getFunctor());
		}

		if (type.equals("general_error")) {
			return new ModelCheckErrorUncovered(
					"An unknown result was uncovered: "
							+ cpt.getArgument(2).toString(), cpt.getArgument(1)
							.getFunctor());
		}

		logger.error("Model checking result unknown. " + cpt.toString());
		// This should not happen unless someone changed the prolog kernel
		throw new IllegalArgumentException("model checking result unknown: "
				+ cpt.toString());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_modelchecking").printNumber(time).openList();
		for (ModelCheckingOptions.Options o : options.getPrologOptions()) {
			pto.printAtom(o.name());
		}
		pto.closeList().printVariable(RESULT).printVariable(STATS).closeTerm();
	}

	public StateSpaceStats getStats() {
		return stats;
	}
}
