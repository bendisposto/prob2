package de.prob.animator.domainobjects;

import de.prob.animator.command.LtlCheckingCommand.PathType;
import de.prob.animator.command.LtlCheckingCommand.Status;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;

public class LtlCheckingResult implements IEvalResult {
	private final Status status;
	private final ListPrologTerm atomics;
	private final PrologTerm structure;
	private final ListPrologTerm counterexample;
	private final PathType pathType;
	private final int loopEntry;
	private final OpInfo[] initPathOps;
	private final StateId stateId;
	private final String code;

	public LtlCheckingResult(final IEvalElement ele, final Status status,
			final ListPrologTerm atomics, final PrologTerm structure,
			final ListPrologTerm counterexample, final PathType pathType,
			final int loopEntry, final OpInfo[] initPathOps,
			final StateId stateid) {
		this.status = status;
		this.atomics = atomics;
		this.structure = structure;
		this.counterexample = counterexample;
		this.pathType = pathType;
		this.loopEntry = loopEntry;
		this.initPathOps = initPathOps;
		this.stateId = stateid;
		this.code = ele.getCode();
	}

	/**
	 * @return the basic outcome of the model-checking, never
	 *         <code>null</code>
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return if the model-checking is finished after the call.
	 */
	public boolean isAbort() {
		return status.isAbort();
	}

	/**
	 * Returns list of pretty-printed sub-formulas, each of the form
	 * ap(Text) or tp(Text).
	 * 
	 * E.g. for G({x=0} U [start]), the list contains ap('{x=0}') and
	 * tp('[start]').
	 * 
	 * @return A list of atomic formulas, never <code>null</code>
	 */
	public ListPrologTerm getAtomics() {
		return atomics;
	}

	/**
	 * A tree structure representing the formula. Atomic propositions or
	 * transition propositions are represented by integers, referring to the
	 * index of the corresponding formula in {@link #atomics}
	 * 
	 * Lets assume that {@link #getAtomics()} returns the list
	 * [ap('{x=0}'),ap('{y=1}'),tp('[start]')] for the LTL formula
	 * 
	 * G({y=1} => ({x=0} U [start])).
	 * 
	 * The corresponding structure would be: globally(implies(1,until(0,2)))
	 * 
	 * @return the structure, <code>null</code> in case of a typecheck-error
	 */
	public PrologTerm getStructure() {
		return structure;
	}

	/**
	 * Returns the list of atoms of the counter-example. Each atom is a term
	 * of the form
	 * atom(StateId,EvalList,NextOperationId,NextOperationString) where
	 * StateId is the ID of the corresponding state and NextOperationId is
	 * the ID of the operation that leads to the next atom.
	 * NextOperationString is a pretty-printed string of that operation.
	 * EvalList is a list of 0s and 1s, it has the same length of the list
	 * returned by {@link #getAtomics()}. Each number represents the
	 * evaluation of the corresponding atomic formula (in
	 * {@link #getAtomics()}) in the current atom. 0=false and 1=true.
	 * 
	 * @return A list of atoms, <code>null</code> if no counter-example is
	 *         found.
	 */
	public ListPrologTerm getCounterexample() {
		return counterexample;
	}

	/**
	 * @return the path type, <code>null</code> if there is no
	 *         counter-example
	 */
	public PathType getPathType() {
		return pathType;
	}

	/**
	 * This value is only defined if {@link #getPathType()} returns LOOP.
	 * 
	 * @return the index in the counter-example where the loop of the
	 *         lasso-form starts, -1 if there is no counterexample or the
	 *         example is not in lasso form.
	 */
	public int getLoopEntry() {
		return loopEntry;
	}

	public OpInfo[] getInitPathOps() {
		return initPathOps;
	}

	public String getStateId() {
		return stateId.getId();
	}

	@Override
	public String getCode() {
		return code;
	}

	public String getValue() {
		return status.toString();
	}
}