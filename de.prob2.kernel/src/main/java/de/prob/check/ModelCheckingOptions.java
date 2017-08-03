package de.prob.check;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class ModelCheckingOptions {

	public static final ModelCheckingOptions DEFAULT = new ModelCheckingOptions()
			.checkDeadlocks(true).checkInvariantViolations(true);
	private final EnumSet<Options> options;

	public enum Options {
		breadth_first_search("breadth_first_search", "breadth first"),
		depth_first_search("depth_first_search", "depth first"),
		find_deadlocks("find_deadlocks", "deadlock check"),
		find_invariant_violations("find_invariant_violations", "invariant check"),
		find_assertion_violations("find_assertion_violations", "assertion check"),
		inspect_existing_nodes("inspect_existing_nodes", "recheck existing states"),
		stop_at_full_coverage("stop_at_full_coverage", "stop at full coverage"),
		partial_order_reduction("partial_order_reduction", "partial order reduction"),
		partial_guard_evaluation("partial_guard_evaluation", "partial guard evaluation"),
		find_goal("find_goal", "search for goal"),
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
		return changeOption(value, Options.breadth_first_search);
	}
	
	public ModelCheckingOptions depthFirst(final boolean value) {
		return changeOption(value, Options.depth_first_search);
	}

	public ModelCheckingOptions checkDeadlocks(final boolean value) {
		return changeOption(value, Options.find_deadlocks);
	}

	public ModelCheckingOptions checkInvariantViolations(final boolean value) {
		return changeOption(value, Options.find_invariant_violations);
	}

	public ModelCheckingOptions checkAssertions(final boolean value) {
		return changeOption(value, Options.find_assertion_violations);
	}

	public ModelCheckingOptions recheckExisting(final boolean value) {
		return changeOption(value, Options.inspect_existing_nodes);
	}

	public ModelCheckingOptions stopAtFullCoverage(final boolean value) {
		return changeOption(value, Options.stop_at_full_coverage);
	}

	public ModelCheckingOptions partialOrderReduction(final boolean value) {
		return changeOption(value, Options.partial_order_reduction);
	}

	public ModelCheckingOptions partialGuardEvaluation(final boolean value) {
		return changeOption(value, Options.partial_guard_evaluation);
	}

	public ModelCheckingOptions checkGoal(final boolean value) {
		return changeOption(value, Options.find_goal);
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
