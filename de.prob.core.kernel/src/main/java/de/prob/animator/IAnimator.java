package de.prob.animator;

import com.google.inject.ImplementedBy;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;

@ImplementedBy(AnimatorImpl.class)
public interface IAnimator {
	/**
	 * Takes a command and executes it.
	 * 
	 * @param command
	 * @throws ProBException
	 */
	public abstract void execute(ICommand command) throws ProBException;

	/**
	 * Takes multiple commands and executes them.
	 * 
	 * @param commands
	 * @throws ProBException
	 */
	public abstract void execute(ICommand... commands) throws ProBException;
	
	
	public void sendInterrupt();
	
}