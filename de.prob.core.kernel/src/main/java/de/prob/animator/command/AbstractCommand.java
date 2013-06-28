package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * The ICommand interface is used to implement composable interactions with the
 * ProB core. It defines two callback methods that are being called by the
 * Animator when the command is being performed.
 * 
 * @author bendisposto
 */
/**
 * @author joy
 *
 */
/**
 * @author joy
 * 
 */
public abstract class AbstractCommand {
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
	 * After performing the query this method receives a Map of bindings from
	 * variable names used in the query to Prolog terms representing the answer.
	 * 
	 * A number of helper tools can be used when processing the results (see
	 * {@link de.prob.parser.BindingGenerator})
	 * 
	 * Note: This method is allowed to throw {@link ResultParserException} if
	 * the answer from Prolog does not match the expectation. The exception is a
	 * subclass of RuntimeException and it should always indicate a bug (or
	 * version inconsistency)
	 * 
	 * @param bindings
	 */
	public abstract void processResult(
			ISimplifiedROMap<String, PrologTerm> bindings);

	/**
	 * Returns the list of subcommands contained in a given
	 * {@link AbstractCommand}. This is called by {@code AnimatorImpl} when run
	 * in debug mode. If developers want individual commands to be executed
	 * separately when an {@link AbstractCommand} is executed, then this method
	 * MUST be overriden. By default, the {@link Collections.#emptyList()} is
	 * returned.
	 * 
	 * @return {@code List} of {@link AbstractCommand} subcommands
	 */
	public List<AbstractCommand> getSubcommands() {
		return Collections.emptyList();
	}
}
