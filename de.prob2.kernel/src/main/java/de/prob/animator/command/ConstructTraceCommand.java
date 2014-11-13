/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.Transition;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

/**
 * Command to execute an event that has not been enumerated by ProB.
 * 
 * @author Jens Bendisposto
 * 
 */
public final class ConstructTraceCommand extends AbstractCommand implements
		IStateSpaceModifier, ITraceDescription {

	Logger logger = LoggerFactory.getLogger(ConstructTraceCommand.class);
	private static final String RESULT_VARIABLE = "Res";
	private static final String ERRORS = "Errors";

	private final List<ClassicalB> evalElement;
	private final State stateId;
	private final List<String> name;
	private final StateSpace stateSpace;
	private final List<Transition> resultTrace = new ArrayList<Transition>();
	private final List<String> errors = new ArrayList<String>();
	private List<Integer> executionNumber = new ArrayList<Integer>();

	public ConstructTraceCommand(final StateSpace s, final State stateId,
			final List<String> name, final List<ClassicalB> predicate,
			final Integer executionNumber) {
		this.stateSpace = s;
		this.stateId = stateId;
		this.name = name;
		this.evalElement = predicate;
		if (name.size() != predicate.size()) {
			throw new IllegalArgumentException(
					"Must provide the same number of names and predicates.");
		}
		for (ClassicalB classicalB : predicate) {
			if (!classicalB.getKind().equals(PREDICATE.toString())) {
				throw new IllegalArgumentException(
						"Formula must be a predicate: " + predicate);
			}
		}
		int size = this.name.size();
		for (int i = 0; i < size; ++i) {
			this.executionNumber.add(executionNumber);
		}
	}

	public ConstructTraceCommand(final StateSpace s, final State stateId,
			final List<String> name, final List<ClassicalB> predicate) {
		this(s, stateId, name, predicate, 1);
	}

	public ConstructTraceCommand(final StateSpace s, final State stateId,
			final List<String> name, final List<ClassicalB> predicate,
			final List<Integer> executionNumber) {
		this(s, stateId, name, predicate);
		this.executionNumber = executionNumber;
		if (name.size() != executionNumber.size()) {
			throw new IllegalArgumentException(
					"Must provide the same number of names and execution numbers.");
		}
	}

	/**
	 * This method is called when the command is prepared for sending. The
	 * method is called by the Animator class, most likely it is not interesting
	 * for other classes.
	 * 
	 * @throws ProBException
	 * 
	 * @see de.prob.animator.command.AbstractCommand#writeCommand(de.prob.prolog.output.IPrologTermOutput)
	 */
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_construct_trace")
				.printAtomOrNumber(stateId.getId());
		pto.openList();
		for (String n : name) {
			pto.printAtom(n);
		}
		pto.closeList();
		final ASTProlog prolog = new ASTProlog(pto, null);
		pto.openList();
		for (ClassicalB cb : evalElement) {
			cb.getAst().apply(prolog);
		}
		pto.closeList();
		pto.openList();
		for (Integer n : executionNumber) {
			pto.printNumber(n);
		}
		pto.closeList();
		pto.printVariable(RESULT_VARIABLE);
		pto.printVariable(ERRORS);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm trace = BindingGenerator.getList(bindings
				.get(RESULT_VARIABLE));

		for (PrologTerm term : trace) {
			CompoundPrologTerm t = BindingGenerator.getCompoundTerm(term, 4);
			Transition operation = Transition.createTransitionFromCompoundPrologTerm(
					stateSpace, t);
			resultTrace.add(operation);
		}

		ListPrologTerm errors = BindingGenerator.getList(bindings.get(ERRORS));
		for (PrologTerm prologTerm : errors) {
			this.errors.add(prologTerm.getFunctor());
		}
	}

	@Override
	public List<Transition> getNewTransitions() {
		return resultTrace;
	}

	public State getFinalState() {
		return resultTrace.get(resultTrace.size() - 1).getDestination();
	}

	@Override
	public Trace getTrace(final StateSpace s) throws RuntimeException {
		Trace t = s.getTrace(stateId.getId());
		return t.addTransitions(resultTrace);
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

}
