package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetInvariantsCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetInvariantsCommand.class);
	private static final String CONJUNCT = "CONJUNCT";
	private static final String LIST = "LIST";
	String invariant;
	List<String> invariantAsList;

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.openTerm("get_invariants").printVariable(CONJUNCT)
				.printVariable(LIST).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {

		try {
			invariant = BindingGenerator.getCompoundTerm(
					bindings.get(CONJUNCT), 0).getFunctor();
			ListPrologTerm prologTerm2 = BindingGenerator.getList(bindings
					.get(LIST));
			ArrayList<String> r = new ArrayList<String>();
			for (PrologTerm prologTerm : prologTerm2) {
				r.add(BindingGenerator.getCompoundTerm(prologTerm, 0)
						.getFunctor());
			}
			invariantAsList = r;
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}

	}

	public String getInvariant() {
		return invariant;
	}

	public List<String> getInvariantAsList() {
		return Collections.unmodifiableList(invariantAsList);
	}

}
