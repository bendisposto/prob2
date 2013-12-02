package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

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
}
