/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelCheckingStepCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "do_modelchecking";

	/**
	 * <p>
	 * The prolog core returns a compound term containing the current stats
	 * about the state space. This term has the arity {@link #STATS_ARITY}.
	 * </p>
	 * 
	 * <p>
	 * The arguments of the term have the following meanings
	 * </p>
	 * <ol>
	 * <li>Total number of nodes in the state space</li>
	 * <li>Total number of transitions in the state space</li>
	 * <li>Number of nodes that have already been processed</li>
	 * </ol>
	 */
	private static final int STATS_ARITY = 3;

	private final int time;
	private final ModelCheckingOptions options;
	private IModelCheckingResult result;
	private static final String RESULT_VARIABLE = "Result";
	private static final String STATS_VARIABLE = "Stats";

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
				bindings.get(STATS_VARIABLE), STATS_ARITY);
		int numberNodes = BindingGenerator.getInteger(statsTerm.getArgument(1))
				.getValue().intValue();
		int numberTrans = BindingGenerator.getInteger(statsTerm.getArgument(2))
				.getValue().intValue();
		int numberProcessed = BindingGenerator
				.getInteger(statsTerm.getArgument(3)).getValue().intValue();

		stats = new StateSpaceStats(numberNodes, numberTrans, numberProcessed);
		result = extractResult(bindings.get(RESULT_VARIABLE));
	}

	private IModelCheckingResult extractResult(final PrologTerm prologTerm) {
		CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(prologTerm,
				prologTerm.getArity());

		String type = cpt.getFunctor();

		switch (type) {
		case "not_yet_finished":
			int maxNodesLeft = BindingGenerator.getInteger(cpt.getArgument(1))
					.getValue().intValue();
			return new NotYetFinished("Model checking not completed",
					maxNodesLeft);
		case "ok":
			return new ModelCheckOk(
					"Model Checking complete. No error nodes found.");
		case "full_coverage":
			return new ModelCheckOk(
					"Model Checking complete. All operations were covered.");
		case "ok_not_all_nodes_considered":
			return new ModelCheckOk(
					"Model Checking complete. No error nodes found. Not all nodes were considered.");
		case "deadlock":
			return new ModelCheckErrorUncovered("Deadlock found.", cpt
					.getArgument(1).getFunctor());
		case "invariant_violation":
			return new ModelCheckErrorUncovered("Invariant violation found.",
					cpt.getArgument(1).getFunctor());
		case "assertion_violation":
			return new ModelCheckErrorUncovered("Assertion violation found.",
					cpt.getArgument(1).getFunctor());
		case "state_error":
			return new ModelCheckErrorUncovered("A state error occured.", cpt
					.getArgument(1).getFunctor());
		case "goal_found":
			return new ModelCheckErrorUncovered("Goal found", cpt
					.getArgument(1).getFunctor());
		case "well_definedness_error":
			return new ModelCheckErrorUncovered(
					"A well definedness error occured.", cpt.getArgument(1)
							.getFunctor());
		case "general_error":
			if (cpt.getArity() == 2)
				return new ModelCheckErrorUncovered(
						"An unknown result was uncovered: "
								+ cpt.getArgument(2).toString(),
						cpt.getArgument(1)
								.getFunctor());
			else
				return new ModelCheckErrorUncovered(
						"A general error occured in state: ", cpt.getArgument(1)
								.getFunctor());

		default:
			logger.error(
					"Model checking result unknown. This should not happen "
							+ "unless someone changed the prolog kernel. Result was: {} ",
					cpt.toString());
			throw new IllegalArgumentException("model checking result unknown: "
					+ cpt.toString());
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printNumber(time).openList();
		for (ModelCheckingOptions.Options o : options.getPrologOptions()) {
			pto.printAtom(o.getPrologName());
		}
		pto.closeList().printVariable(RESULT_VARIABLE).printVariable(STATS_VARIABLE).closeTerm();
	}

	public StateSpaceStats getStats() {
		return stats;
	}
}
