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

	/**
	 * Kills the underlying probcli
	 */
	void kill();
	
	/**
	 * Signals the {@link IAnimator} that a transaction is beginning. The
	 * {@link IAnimator} can then set a flag indicating that it is busy because
	 * the {@link IAnimator} is likely to be blocked for a long period of time.
	 */
	void startTransaction();

	/**
	 * Signals the {@link IAnimator} that a transaction has ended. The
	 * {@link IAnimator} can then reset the flag indicating that it is busy.
	 */
	void endTransaction();

	/**
	 * @return <code>true</code> if the animator is busy and <code>false</code>
	 *         otherwise. While <code>true</code>, the caller of the
	 *         {@link IAnimator} should not call
	 *         {@link #execute(AbstractCommand...)}.
	 */
	boolean isBusy();

	/**
	 * @return unique id associated with this instance of the animator. All
	 *         implementations should ensure that this id is unique.
	 */
	String getId();
}
