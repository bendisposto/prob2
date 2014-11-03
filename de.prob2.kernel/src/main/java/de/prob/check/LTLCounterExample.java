package de.prob.check;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.command.LtlCheckingCommand.PathType;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.LTL;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class LTLCounterExample implements IModelCheckingResult, IEvalResult,
		ITraceDescription {

	private final LTL formula;
	private final List<OpInfo> pathToCE;
	private final List<OpInfo> counterExample;
	private final int loopEntry;
	private final PathType pathType;

	public LTLCounterExample(final LTL formula, final List<OpInfo> pathToCE,
			final List<OpInfo> counterExample, final int loopEntry,
			final PathType pathType) {
		this.formula = formula;
		this.pathToCE = pathToCE;
		this.counterExample = counterExample;
		this.loopEntry = loopEntry;
		this.pathType = pathType;

	}

	public OpInfo getLoopEntry() {
		if (loopEntry == -1) {
			return null;
		}
		return counterExample.get(loopEntry);
	}

	public PathType getPathType() {
		return pathType;
	}

	public List<OpInfo> getOpList() {
		List<OpInfo> ops = new ArrayList<OpInfo>();
		ops.addAll(pathToCE);
		ops.addAll(counterExample);
		return ops;
	}

	public String getCode() {
		return formula.getCode();
	}

	@Override
	public String getMessage() {
		return "LTL counterexample found";
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		Trace t = new Trace(s);
		for (OpInfo op : pathToCE) {
			t = t.add(op.getId());
		}
		for (OpInfo op : counterExample) {
			t = t.add(op.getId());
		}
		return t;
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
