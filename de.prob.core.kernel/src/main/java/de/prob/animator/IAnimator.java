package de.prob.animator;

import com.google.inject.ImplementedBy;

import de.prob.animator.command.AbstractCommand;

@ImplementedBy(AnimatorImpl.class)
public interface IAnimator {
	/**
	 * Takes a command and executes it.
	 * 
	 * @param command
	 */
	public abstract void execute(AbstractCommand command);

	/**
	 * Takes multiple commands and executes them.
	 * 
	 * @param commands
	 */
	public abstract void execute(AbstractCommand... commands);

	public void sendInterrupt();

}