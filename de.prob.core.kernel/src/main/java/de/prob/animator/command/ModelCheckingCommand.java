/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingResult;
import de.prob.check.StateSpaceStats;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

public class ModelCheckingCommand extends AbstractCommand {
	/**
	 * <p>
	 * The prolog core calculates a list of new operations after performing a
	 * model checking step. These have the arity {@link #OP_ARITY}.
	 * </p>
	 * <p>
	 * The operations have the following meanings:
	 * </p>
	 * <ol>
	 * <li>Operation Id</li>
	 * <li>Source Id</li>
	 * <li>Destination Id</li>
	 * <ol>
	 */
	public final static int OP_ARITY = 3;

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
	public final static int STATS_ARITY = 3;

	private final int time;
	private final ModelCheckingOptions options;
	private ModelCheckingResult result;
	private final String RESULT = "Result";
	private final String OPS = "Ops";
	private final String STATS = "Stats";
	private StateSpaceStats stats;

	Logger logger = LoggerFactory.getLogger(ModelCheckingCommand.class);
	private final long last;
	private final List<OpInfo> newOps = new ArrayList<OpInfo>();

	public ModelCheckingCommand(final int time,
			final ModelCheckingOptions options, final long last) {
		this.time = time;
		this.options = options;
		this.last = last;
	}

	public ModelCheckingResult getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm pt = bindings.get(RESULT);
		CompoundPrologTerm term = BindingGenerator.getCompoundTerm(pt,
				pt.getArity());
		ListPrologTerm list = BindingGenerator.getList(bindings.get(OPS));
		result = new ModelCheckingResult(term);

		for (PrologTerm prologTerm : list) {
			CompoundPrologTerm op = BindingGenerator.getCompoundTerm(
					prologTerm, OP_ARITY);
			String id = OpInfo.getIdFromPrologTerm(op.getArgument(1));
			String src = OpInfo.getIdFromPrologTerm(op.getArgument(2));
			String dest = OpInfo.getIdFromPrologTerm(op.getArgument(3));
			newOps.add(new OpInfo(id, src, dest));
		}

		CompoundPrologTerm statsTerm = BindingGenerator.getCompoundTerm(
				bindings.get(STATS), STATS_ARITY);
		int numberNodes = BindingGenerator.getInteger(statsTerm.getArgument(1))
				.getValue().intValue();
		int numberTrans = BindingGenerator.getInteger(statsTerm.getArgument(2))
				.getValue().intValue();
		int numberProcessed = BindingGenerator
				.getInteger(statsTerm.getArgument(3)).getValue().intValue();
		stats = new StateSpaceStats(numberNodes, numberTrans, numberProcessed);

	}

	public StateSpaceStats getStats() {
		return stats;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_modelcheck").printNumber(time).openList();
		for (ModelCheckingOptions.Options o : options.getPrologOptions()) {
			pto.printAtom(o.name());
		}
		pto.closeList().printNumber(last).printVariable(OPS)
				.printVariable(RESULT).printVariable(STATS).closeTerm();
	}

	public List<OpInfo> getNewOps() {
		return newOps;
	}
}
