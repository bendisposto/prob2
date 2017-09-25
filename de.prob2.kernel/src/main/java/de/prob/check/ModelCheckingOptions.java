package de.prob.check;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class ModelCheckingOptions {

	public static final ModelCheckingOptions DEFAULT = new ModelCheckingOptions()
			.checkDeadlocks(true).checkInvariantViolations(true);
	private final EnumSet<Options> options;

	public enum Options {
		BREADTH_FIRST_SEARCH("breadth_first_search", "breadth first"),
		DEPTH_FIRST_SEARCH("depth_first_search", "depth first"),
		FIND_DEADLOCKS("find_deadlocks", "deadlock check"),
		FIND_INVARIANT_VIOLATIONS("find_invariant_violations", "invariant check"),
		FIND_ASSERTION_VIOLATIONS("find_assertion_violations", "assertion check"),
		INSPECT_EXISTING_NODES("inspect_existing_nodes", "recheck existing states"),
		STOP_AT_FULL_COVERAGE("stop_at_full_coverage", "stop at full coverage"),
		PARTIAL_ORDER_REDUCTION("partial_order_reduction", "partial order reduction"),
		PARTIAL_GUARD_EVALUATION("partial_guard_evaluation", "partial guard evaluation"),
		FIND_GOAL("find_goal", "search for goal"),
		;

		private final String prologName;
		private final String description;

		private Options(final String prologName, final String description) {
			this.prologName = prologName;
			this.description = description;
		}
		
		public String getPrologName() {
			return this.prologName;
		}
		
		public String getDescription() {
			return description;
		}
	}

	public ModelCheckingOptions() {
		options = EnumSet.noneOf(Options.class);
	}

	private ModelCheckingOptions(final EnumSet<Options> options) {
		this.options = options;
	}

	public ModelCheckingOptions breadthFirst(final boolean value) {
		return changeOption(value, Options.BREADTH_FIRST_SEARCH);
	}
	
	public ModelCheckingOptions depthFirst(final boolean value) {
		return changeOption(value, Options.DEPTH_FIRST_SEARCH);
	}

	public ModelCheckingOptions checkDeadlocks(final boolean value) {
		return changeOption(value, Options.FIND_DEADLOCKS);
	}

	public ModelCheckingOptions checkInvariantViolations(final boolean value) {
		return changeOption(value, Options.FIND_INVARIANT_VIOLATIONS);
	}

	public ModelCheckingOptions checkAssertions(final boolean value) {
		return changeOption(value, Options.FIND_ASSERTION_VIOLATIONS);
	}

	public ModelCheckingOptions recheckExisting(final boolean value) {
		return changeOption(value, Options.INSPECT_EXISTING_NODES);
	}

	public ModelCheckingOptions stopAtFullCoverage(final boolean value) {
		return changeOption(value, Options.STOP_AT_FULL_COVERAGE);
	}

	public ModelCheckingOptions partialOrderReduction(final boolean value) {
		return changeOption(value, Options.PARTIAL_ORDER_REDUCTION);
	}

	public ModelCheckingOptions partialGuardEvaluation(final boolean value) {
		return changeOption(value, Options.PARTIAL_GUARD_EVALUATION);
	}

	public ModelCheckingOptions checkGoal(final boolean value) {
		return changeOption(value, Options.FIND_GOAL);
	}

	private ModelCheckingOptions changeOption(final boolean value,
			final Options o) {
		if (value == options.contains(o)) {
			return this;
		}

		EnumSet<Options> copyOf = EnumSet.copyOf(options);
		if (value) {
			copyOf.add(o);
		} else {
			copyOf.remove(o);
		}
		return new ModelCheckingOptions(copyOf);
	}

	public Set<Options> getPrologOptions() {
		return Collections.unmodifiableSet(options);
	}

	@Override
	public String toString() {
		return options.toString();
	}

}
