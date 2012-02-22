package de.prob.animator;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.model.IStateSpace;

public class StateSpaceProvider implements Provider<IStateSpace> {
	private IStateSpace current = null;
	private final Provider<IAnimator> pa;

	@Inject
	public StateSpaceProvider(final Provider<IAnimator> pa) {
		this.pa = pa;
	}

	@Override
	public IStateSpace get() {
		System.out.print(pa.get());
		System.out.print(pa.get());
		return null;
	}

}
