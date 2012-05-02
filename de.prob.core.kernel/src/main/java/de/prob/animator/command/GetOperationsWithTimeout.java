package de.prob.animator.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Finds the operations that have a timeout for a specific state
 * 
 * @author joy
 * 
 */
public class GetOperationsWithTimeout implements ICommand {

	private final Logger logger = LoggerFactory
			.getLogger(GetOperationsWithTimeout.class);

	private static final String TIMEOUT_VARIABLE = "TO";
	private final String state;
	private List<String> timeouts;

	public GetOperationsWithTimeout(final String state) {
		this.state = state;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		try {
			timeouts = PrologTerm.atomicStrings(BindingGenerator.getList(
					bindings, TIMEOUT_VARIABLE));
		} catch (ResultParserException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ProBException();
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("op_timeout_occurred").printAtomOrNumber(state)
				.printVariable(TIMEOUT_VARIABLE).closeTerm();
	}

	public List<String> getTimeouts() {
		return timeouts;
	}
}
