package de.prob.model.eventb.algorithm;

import java.util.EnumSet;

import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingOptions.Options;

public class AlgorithmGenerationOptions {
	public static AlgorithmGenerationOptions DEFAULT = new AlgorithmGenerationOptions()
	.mergeBranches(true).optimize(true).propagateAssertions(true)
	private final EnumSet<Options> options;

	public enum Options {
		mergeBranches, optimize, propagateAssertions
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

	public boolean isMergeBranches() {
		return options.contains(Options.mergeBranches)
	}

	public boolean isOptimize() {
		return options.contains(Options.optimize)
	}

	public boolean isPropagateAssertions() {
		return options.contains(Options.propagateAssertions)
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
