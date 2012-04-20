/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.ProBException;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Command to execute an event that has not been enumerated by ProB, for further
 * information see ({@link #getOperation})
 * 
 * @author Jens Bendisposto
 * 
 */
public final class GetOperationByPredicateCommand implements ICommand {

	Logger logger = LoggerFactory
			.getLogger(GetOperationByPredicateCommand.class);
	private static final String NEW_STATE_ID_VARIABLE = "NewStateID";
	private final ClassicalBEvalElement evalElement;
	private final String stateId;
	private final String name;
	private final List<OpInfo> operation = new ArrayList<OpInfo>();
	private final int nrOfSolutions;

	public GetOperationByPredicateCommand(final String stateId,
			final String name, final String predicate, final int nrOfSolutions) {
		this.stateId = stateId;
		this.name = name;
		this.nrOfSolutions = nrOfSolutions;
		this.evalElement = new ClassicalBEvalElement(predicate);
	}

	/**
	 * This method is called when the command is prepared for sending. The
	 * method is called by the Animator class, most likely it is not interesting
	 * for other classes.
	 * 
	 * @throws ProBException
	 * 
	 * @see de.prob.core.command.IComposableCommand#writeCommand(de.prob.prolog.output.IPrologTermOutput)
	 */
	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.openTerm("execute_custom_operations").printAtomOrNumber(stateId)
				.printAtom(name);
		try {
			final ASTProlog prolog = new ASTProlog(pto, null);
			evalElement.parse().apply(prolog);
		} catch (BException e) {
			logger.error("Parse error", e);
			throw new ProBException();
		} finally {
			pto.printNumber(nrOfSolutions);
			pto.printVariable(NEW_STATE_ID_VARIABLE);
			pto.printVariable("Errors").closeTerm();
		}
	}

	/**
	 * This method is called to extract relevant information from ProB's answer.
	 * The method is called by the Animator class, most likely it is not
	 * interesting for other classes.
	 * 
	 * @throws ProBException
	 * 
	 * @see de.prob.core.command.IComposableCommand#writeCommand(de.prob.prolog.output.IPrologTermOutput)
	 */
	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {

		operation.clear();

		try {
			ListPrologTerm list = BindingGenerator.getList(bindings
					.get(NEW_STATE_ID_VARIABLE));

			if (!list.isEmpty()) {
				for (PrologTerm prologTerm : list) {
					CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(
							prologTerm, 6);
					operation.add(new OpInfo(cpt));
				}
			}
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}
	}

	public List<OpInfo> getOperations() {
		return operation;
	}

}
