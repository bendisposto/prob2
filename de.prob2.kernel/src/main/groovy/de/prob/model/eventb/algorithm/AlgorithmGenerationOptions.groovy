package de.prob.model.eventb.algorithm;


public class AlgorithmGenerationOptions {
	public static AlgorithmGenerationOptions DEFAULT = new AlgorithmGenerationOptions()
	.mergeBranches(true).optimize(true).propagateAssertions(true)
	private final EnumSet<Options> options;

	public enum Options {
		mergeBranches, optimize, propagateAssertions, terminationAnalysis, loopEvent
	}

	public AlgorithmGenerationOptions() {
		options = EnumSet.noneOf(Options.class)
	}

	private AlgorithmGenerationOptions(final EnumSet<Options> options) {
		this.options = options
	}

	public AlgorithmGenerationOptions mergeBranches(final boolean value) {
		changeOption(value, Options.mergeBranches)
	}

	public AlgorithmGenerationOptions optimize(final boolean value) {
		changeOption(value, Options.optimize)
	}

	public AlgorithmGenerationOptions propagateAssertions(final boolean value) {
		changeOption(value, Options.propagateAssertions)
	}

	public AlgorithmGenerationOptions terminationAnalysis(final boolean value) {
		changeOption(value, Options.terminationAnalysis)
	}
	
	public AlgorithmGenerationOptions loopEvent(final boolean value) {
		changeOption(value, Options.loopEvent)
	}

	public boolean isMergeBranches() {
		return options.contains(Options.mergeBranches)
	}

	public boolean isOptimize() {
		return options.contains(Options.optimize)
	}

	public boolean isPropagateAssertions() {
		return options.contains(Options.propagateAssertions)
	}

	public boolean isTerminationAnalysis() {
		return options.contains(Options.terminationAnalysis)
	}
	
	public boolean isLoopEvent() {
		return options.contains(Options.loopEvent)
	}

	private AlgorithmGenerationOptions changeOption(final boolean value,
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
		return new AlgorithmGenerationOptions(copyOf);
	}

	public Set<Options> getOptions() {
		return Collections.unmodifiableSet(options);
	}
}
