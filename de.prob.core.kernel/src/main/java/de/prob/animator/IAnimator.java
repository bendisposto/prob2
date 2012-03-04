package de.prob.animator;

import com.google.inject.ImplementedBy;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;

@ImplementedBy(AnimatorImpl.class)
public interface IAnimator {
	public abstract void execute(ICommand command) throws ProBException;

	public abstract void execute(ICommand... commands) throws ProBException;
}