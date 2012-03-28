package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.model.StringWithLocation;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetInvariantsCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetInvariantsCommand.class);
	private static final String LIST = "LIST";
	List<StringWithLocation> invariant;

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.openTerm("get_invariants").printVariable(LIST).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		ArrayList<StringWithLocation> r = new ArrayList<StringWithLocation>();

		try {

			PrologTerm prologTerm = bindings.get(LIST);
			ListPrologTerm invs = BindingGenerator.getList(prologTerm);
			for (PrologTerm invTerm : invs) {
				CompoundPrologTerm inv = BindingGenerator.getCompoundTerm(
						invTerm, 2);
				System.out.print(inv.getArgument(2));
				System.out.print(" -> ");
				System.out.println(inv.getArgument(1));
			}

		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		} finally {
			invariant = r;
		}
	}

	public List<StringWithLocation> getInvariant() {
		return invariant;
	}

}
