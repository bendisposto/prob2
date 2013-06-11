/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import de.prob.check.ModelCheckingResult;
import de.prob.exception.ProBLoggerFactory;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

public class ConsistencyCheckingCommand extends AbstractCommand {
	private final int time;
	private final List<String> options;
	private ModelCheckingResult result;
	private final String RESULT = "Result";
	private final String OPS = "Ops";

	Logger logger = ProBLoggerFactory
			.getLogger(ConsistencyCheckingCommand.class);
	private final long last;
	private final List<OpInfo> newOps = new ArrayList<OpInfo>();

	public ConsistencyCheckingCommand(final int time,
			final List<String> options, final long last) {
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
					prologTerm, 3);
			String id = OpInfo.getIdFromPrologTerm(op.getArgument(1));
			String src = OpInfo.getIdFromPrologTerm(op.getArgument(2));
			String dest = OpInfo.getIdFromPrologTerm(op.getArgument(3));
			newOps.add(new OpInfo(id, src, dest));
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_modelcheck").printNumber(time).openList();
		for (String o : options) {
			pto.printAtom(o);
		}
		pto.closeList().printNumber(last).printVariable(OPS)
				.printVariable(RESULT).closeTerm();
	}

	public List<OpInfo> getNewOps() {
		return newOps;
	}
}
