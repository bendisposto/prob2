package de.prob.check;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.command.LtlCheckingCommand.PathType;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.LTL;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class LTLCounterExample implements IModelCheckingResult, IEvalResult,
		ITraceDescription {

	private final LTL formula;
	private final List<Transition> pathToCE;
	private final List<Transition> counterExample;
	private final int loopEntry;
	private final PathType pathType;

	public LTLCounterExample(final LTL formula,
			final List<Transition> pathToCE,
			final List<Transition> counterExample, final int loopEntry,
			final PathType pathType) {
		this.formula = formula;
		this.pathToCE = pathToCE;
		this.counterExample = counterExample;
		this.loopEntry = loopEntry;
		this.pathType = pathType;

	}

	public Transition getLoopEntry() {
		if (loopEntry == -1) {
			return null;
		}
		return counterExample.get(loopEntry);
	}

	public PathType getPathType() {
		return pathType;
	}

	public List<Transition> getOpList() {
		List<Transition> ops = new ArrayList<Transition>();
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
		t = t.addTransitions(pathToCE);
		t = t.addTransitions(counterExample);
		return t;
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
