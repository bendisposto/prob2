/**
 * 
 */
package de.prob.animator.command.notImplemented;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.command.ICommand;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.check.ModelCheckingResult;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command makes ProB search for a invariant violation with an optional
 * selection of events.
 * 
 * @author plagge
 */
public class ConstraintBasedInvariantCheckCommand implements ICommand {

	Logger logger = LoggerFactory
			.getLogger(ConstraintBasedInvariantCheckCommand.class);

	public static class InvariantCheckCounterExample {
		private final String eventName;
		private final OpInfo step1, step2;

		public InvariantCheckCounterExample(final String eventName,
				final OpInfo step1, final OpInfo step2) {
			this.eventName = eventName;
			this.step1 = step1;
			this.step2 = step2;
		}

		public String getEventName() {
			return eventName;
		}

		public OpInfo getStep1() {
			return step1;
		}

		public OpInfo getStep2() {
			return step2;
		}
	}

	private static final String COMMAND_NAME = "invariant_check";
	private static final String RESULT_VARIABLE = "R";

	private final Collection<String> events;

	private ModelCheckingResult result;
	private Collection<InvariantCheckCounterExample> counterexamples;

	/**
	 * @param events
	 *            is a collection of names of that events that should be
	 *            checked. May be <code>null</code>. In that case, all events
	 *            are checked.
	 */
	public ConstraintBasedInvariantCheckCommand(final Collection<String> events) {
		this.events = events == null ? null : Collections
				.unmodifiableCollection(new ArrayList<String>(events));
	}

	public Collection<String> getEvents() {
		return events;
	}

	public Collection<InvariantCheckCounterExample> getCounterExamples() {
		return counterexamples;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		if (events != null && !events.isEmpty()) {
			pto.openTerm("ops");
			pto.openList();
			for (final String event : events) {
				pto.printAtom(event);
			}
			pto.closeList();
			pto.closeTerm();
		} else {
			pto.printAtom("all");
		}
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		;
		final Collection<InvariantCheckCounterExample> counterexamples;
		if (resultTerm.hasFunctor("interrupted", 0)) {
			result = new ModelCheckingResult(
					ModelCheckingResult.Result.not_yet_finished);
			counterexamples = null;
		} else if (resultTerm.isList()) {
			ListPrologTerm ceTerm = (ListPrologTerm) resultTerm;
			result = ceTerm.isEmpty() ? new ModelCheckingResult(
					ModelCheckingResult.Result.ok) : new ModelCheckingResult(
					ModelCheckingResult.Result.invariant_violation);
			counterexamples = Collections
					.unmodifiableCollection(extractExamples(ceTerm));
		} else {
			String msg = "unexpected result from invariant check: "
					+ resultTerm;
			logger.error(msg);
			throw new ProBError(msg);
		}
		this.counterexamples = counterexamples;
	}

	private Collection<InvariantCheckCounterExample> extractExamples(
			final ListPrologTerm ceTerm) {
		Collection<InvariantCheckCounterExample> examples = new ArrayList<ConstraintBasedInvariantCheckCommand.InvariantCheckCounterExample>();
		for (final PrologTerm t : ceTerm) {
			final CompoundPrologTerm term = (CompoundPrologTerm) t;
			final String eventName = PrologTerm.atomicString(term
					.getArgument(1));
			final OpInfo step1 = new OpInfo(
					(CompoundPrologTerm) term.getArgument(2));
			final OpInfo step2 = new OpInfo(
					(CompoundPrologTerm) term.getArgument(3));
			final InvariantCheckCounterExample ce = new InvariantCheckCounterExample(
					eventName, step1, step2);
			examples.add(ce);
		}
		return examples;
	}

	public ModelCheckingResult getResult() {
		return result;
	}
}
