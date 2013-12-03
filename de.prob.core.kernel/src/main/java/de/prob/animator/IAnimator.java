package de.prob.animator;

import com.google.inject.ImplementedBy;

import de.prob.animator.command.AbstractCommand;

/**
 * This interface provides the methods needed to access the ProB prolog kernel.
 * The user can specific tasks to execute via the {@link AbstractCommand}
 * abstraction and either the {@link #execute(AbstractCommand)} or
 * {@link #execute(AbstractCommand...)} methods. If an execution should be
 * broken off, the {@link #sendInterrupt()} method should be called.
 * 
 * @author joy
 * 
 */
@ImplementedBy(AnimatorImpl.class)
public interface IAnimator {
	/**
	 * Takes an {@link AbstractCommand} and executes it.
	 * 
	 * @param command
	 *            an {@link AbstractCommand} to execute
	 */
	void execute(AbstractCommand command);

	/**
	 * Takes multiple commands and executes them.
	 * 
	 * @param commands
	 *            multiple {@link AbstractCommand}s to execute
	 */
	void execute(AbstractCommand... commands);

	/**
	 * Interrupt any commands that are currently being executed.
	 */
	void sendInterrupt();
}
