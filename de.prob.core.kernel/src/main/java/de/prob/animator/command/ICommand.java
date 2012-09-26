package de.prob.animator.command;

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
public interface ICommand {
	/**
	 * Creates the prolog term that is sent to the core. It gets the term output
	 * object from the animator. The animator will automatically take care of
	 * name clashes when Prolog variables are used.
	 * 
	 * @param pto
	 *            {@link de.prob.prolog.output.IPrologTermOutput} that must be used to write the query
	 *            term.
	 */
	void writeCommand(IPrologTermOutput pto);

	/**
	 * After performing the query this method receives a Map of bindings from
	 * variable names used in the query to Prolog terms representing the answer.
	 * 
	 * A number of helper tools shall be used when processing the results (see
	 * {@link de.prob.parser.BindingGenerator})
	 * 
	 * Note: This method is allowed to throw {@link ResultParserException} if
	 * the answer from Prolog does not match the expectation. The exception is a
	 * subclass of RuntimeException and it should always indicate a bug (or
	 * version inconsistency)
	 * 
	 * @param bindings
	 */
	void processResult(ISimplifiedROMap<String, PrologTerm> bindings);
}
