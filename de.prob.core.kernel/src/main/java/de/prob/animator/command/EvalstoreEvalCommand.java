/**
 * 
 */
package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * @author plagge
 * 
 */
public class EvalstoreEvalCommand extends AbstractCommand {

	private static final String RESULT_VAR = "Result";
	private final long evalstoreId;
	private final IEvalElement evalElement;
	private final Integer timeout;

	private EvalstoreResult result;

	public EvalstoreEvalCommand(final long evalstoreId,
			final IEvalElement evalElement, final Integer timeout) {
		super();
		this.evalstoreId = evalstoreId;
		this.evalElement = evalElement;
		this.timeout = timeout;
	}

	public EvalstoreEvalCommand(final long evalstoreId,
			final IEvalElement evalElement) {
		this(evalstoreId, evalElement, null);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("es_eval");
		pto.printNumber(evalstoreId);
		evalElement.printProlog(pto);
		if (timeout == null) {
			pto.printAtom("none");
		} else {
			pto.printNumber(timeout);
		}
		pto.printVariable(RESULT_VAR);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm term = (CompoundPrologTerm) bindings.get(RESULT_VAR);

		// TODO[DP,23.01.2013] Check with EvaluationResult: I don't know what
		// most fields are about
		final EvalstoreResult result;
		if (term.hasFunctor("interrupted", 0)) {
			result = new EvalstoreResult(false, true, evalstoreId, null,
					Collections.<String> emptyList());
		} else if (term.hasFunctor("timeout", 0)) {
			result = new EvalstoreResult(true, false, evalstoreId, null,
					Collections.<String> emptyList());
		} else if (term.hasFunctor("errors", 1)) {
			final ListPrologTerm args = (ListPrologTerm) term.getArgument(1);
			final List<String> errors = new ArrayList<String>(args.size());
			for (final PrologTerm arg : args) {
				errors.add(PrologTerm.atomicString(((CompoundPrologTerm) arg)
						.getArgument(1)));
			}
			final String error = errors.isEmpty() ? "unspecified error"
					: errors.get(0);
			final IEvalResult er = new ComputationNotCompletedResult(
					evalElement.getCode(), error);
			result = new EvalstoreResult(false, false, evalstoreId, er,
					Collections.<String> emptyList());
		} else if (term.hasFunctor("ok", 4)) {
			// first argument ignored
			final String valueStr = PrologTerm
					.atomicString(term.getArgument(2));
			final ListPrologTerm ids = (ListPrologTerm) term.getArgument(3);
			final List<String> newIdentifiers = PrologTerm.atomicStrings(ids);
			final long storeId = ((IntegerPrologTerm) term.getArgument(4))
					.getValue().longValue();
			final EvalResult er = new EvalResult(evalElement.getCode(),
					valueStr, null, new HashMap<String, String>(),
					new HashMap<String, PrologTerm>());
			result = new EvalstoreResult(false, false, storeId, er,
					newIdentifiers);
		} else {
			// TODO[DP,23.01.2013] This should be some sensible exception - but
			// I don't now which
			throw new IllegalStateException("Unexpected es_eval result: "
					+ term.getFunctor() + "/" + term.getArity());
		}
		this.result = result;
	}

	public EvalstoreResult getResult() {
		return result;
	}

	public static class EvalstoreResult {
		private final boolean hasTimeoutOccurred;
		private final boolean hasInterruptedOccurred;
		private final long resultingStoreId;
		private final IEvalResult result;
		private final List<String> newIdentifiers;

		public EvalstoreResult(final boolean hasTimeoutOccurred,
				final boolean hasInterruptedOccurred,
				final long resultingStoreId, final IEvalResult result,
				final List<String> newIdentifiers) {
			super();
			this.hasTimeoutOccurred = hasTimeoutOccurred;
			this.hasInterruptedOccurred = hasInterruptedOccurred;
			this.resultingStoreId = resultingStoreId;
			this.result = result;
			this.newIdentifiers = newIdentifiers;
		}

		public boolean hasTimeoutOccurred() {
			return hasTimeoutOccurred;
		}

		public boolean hasInterruptedOccurred() {
			return hasInterruptedOccurred;
		}

		public boolean isSuccess() {
			return !hasTimeoutOccurred && !hasInterruptedOccurred
					&& result instanceof EvalResult;
		}

		public long getResultingStoreId() {
			return resultingStoreId;
		}

		public IEvalResult getResult() {
			return result;
		}

		public List<String> getNewIdentifiers() {
			return newIdentifiers;
		}

		@Override
		public String toString() {
			final String str;
			if (hasTimeoutOccurred) {
				str = "*timeout*";
			} else if (hasInterruptedOccurred) {
				str = "*interrupted*";
			} else {
				str = result.toString();
			}
			return str;
		}
	}

}
