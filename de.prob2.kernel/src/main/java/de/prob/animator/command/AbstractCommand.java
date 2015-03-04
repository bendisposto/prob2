package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import de.prob.animator.IPrologResult;
import de.prob.animator.InterruptedResult;
import de.prob.animator.NoResult;
import de.prob.animator.YesResult;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * The {@link AbstractCommand} class is used to implement composable
 * interactions with the ProB core. It defines two callback methods that are
 * being called by the Animator when the command is being performed. It also
 * provides a {@link #getSubcommands()} method to break down the command into
 * separate commands to improve debugging.
 * 
 * @author joy
 * 
 */
public abstract class AbstractCommand {

	protected boolean interrupted = false;
	protected boolean completed = true;

	/**
	 * Creates the prolog term that is sent to the core. It gets the term output
	 * object from the animator. The animator will automatically take care of
	 * name clashes when Prolog variables are used.
	 * 
	 * @param pto
	 *            {@link de.prob.prolog.output.IPrologTermOutput} that must be
	 *            used to write the query term.
	 */
	public abstract void writeCommand(IPrologTermOutput pto);

	/**
	 * <p>
	 * After performing the query this method receives a Map of bindings from
	 * variable names used in the query to Prolog terms representing the answer.
	 * </p>
	 * 
	 * <p>
	 * A number of helper tools can be used when processing the results (see
	 * {@link de.prob.parser.BindingGenerator})
	 * </p>
	 * 
	 * <p>
	 * This will be called if the Prolog query was successful and no error
	 * messages were logged during the execution of the query. If the query was
	 * not successful, or if there were errors
	 * {@link AbstractCommand#processErrorResult(ISimplifiedROMap, String)} will
	 * be called.
	 * </p>
	 * 
	 * <p>
	 * Note: This method is allowed to throw {@link ResultParserException} if
	 * the answer from Prolog does not match the expectation. The exception is a
	 * subclass of RuntimeException and it should always indicate a bug (or
	 * version inconsistency)
	 * </p>
	 * 
	 * @param bindings
	 *            {@link ISimplifiedROMap} of String variable names to their
	 *            calculated answers represented as {@link PrologTerm}s
	 */
	public abstract void processResult(
			ISimplifiedROMap<String, PrologTerm> bindings);

	/**
	 * Returns the list of sub-commands contained in a given
	 * {@link AbstractCommand}. This allow the animator to debug the code. If
	 * developers want individual commands to be executed separately in debug
	 * mode when an {@link AbstractCommand} is executed, then this method MUST
	 * be overridden. By default, {@link Collections#emptyList()} is returned.
	 * 
	 * @return {@code List} of {@link AbstractCommand} subcommands
	 */
	public List<AbstractCommand> getSubcommands() {
		return Collections.emptyList();
	}

	public boolean isCompleted() {
		return interrupted || completed
				|| Thread.currentThread().isInterrupted();
	}

	/**
	 * This method determines if the animator should be blocked during the
	 * execution of the command. This is the case when dealing with a command
	 * that executes multiple times (i.e a model checking command).
	 * 
	 * @return true, if an the animator status should be broadcast as blocked.
	 *         false, otherwise.
	 */
	public boolean blockAnimator() {
		return false;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * This code is called in three cases:
	 * <ol>
	 * <li>The Prolog query was unsuccessful (answered no) and there were no
	 * errors logged.</li>
	 * <li>The Prolog query was unsuccessful (answered no) and errors were found
	 * </li>
	 * <li>The Prolog query was successful (and bindings have been generated),
	 * but errors were also found</li>
	 * </ol>
	 * 
	 * Default behavior for error handling is implemented in
	 * {@link AbstractCommand}, but if a developer wants to implement special
	 * behavior, he/she needs to overwrite this method.
	 * 
	 * @param result
	 *            is <code>null</code> if Prolog answered no or a list of
	 *            bindings if the execution of the Prolog query was successful.
	 * @param errormessages
	 *            contains a {@link String} listing the error messages or
	 *            <code>null</code> if no error messages were logged.
	 */
	public void processErrorResult(final IPrologResult result,
			final List<String> errormessages) {
		if (result instanceof NoResult) {
			String message = "Prolog said no.";
			if (errormessages != null) {
				message += " Error messages were: "
						+ Joiner.on("\n").join(errormessages);
			} else {
				message += " No error messages were produced.";
			}
			throw new ProBError(message);
		} else if (result instanceof InterruptedResult) {
			interrupted = true;
		} else if (result instanceof YesResult) {
			processResult(((YesResult) result).getBindings());
			throw new ProBError("ProB reported Errors: " + errormessages);
		} else {
			throw new ProBError("Errors were: "
					+ Joiner.on("\n").join(errormessages));
		}
	}
}
