package de.prob.animator;

import com.google.inject.ImplementedBy;

import de.prob.animator.command.ICommand;
import de.prob.exception.UnexpectedResultException;

@ImplementedBy(AnimatorImpl.class)
public interface IAnimator {
	/**
	 * Takes a command and executes it.
	 * 
	 * @param command
	 */
	public abstract void execute(ICommand command);

	/**
	 * Takes multiple commands and executes them.
	 * 
	 * @param commands
	 */
	public abstract void execute(ICommand... commands);

	public void sendInterrupt();

}