package de.prob.animator.command.representation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.model.representation.NamedEntity;
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
	List<NamedEntity> invariant;
	private final NodeIdAssignment nodeIdMapping;

	public GetInvariantsCommand(final NodeIdAssignment nodeIdMapping) {
		this.nodeIdMapping = nodeIdMapping;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.openTerm("get_invariants").printVariable(LIST).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		ArrayList<NamedEntity> r = new ArrayList<NamedEntity>();

		try {

			PrologTerm prologTerm = bindings.get(LIST);
			ListPrologTerm invs = BindingGenerator.getList(prologTerm);
			for (PrologTerm invTerm : invs) {
				CompoundPrologTerm inv = BindingGenerator.getCompoundTerm(
						invTerm, 2);
				int id = Integer.parseInt(inv.getArgument(2).toString());
				String name = inv.getArgument(1).toString();
				Node ID = nodeIdMapping.lookupById(id);
				r.add(new NamedEntity(name, ID));
			}

		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		} finally {
			invariant = r;
		}
	}

	public List<NamedEntity> getInvariants() {
		return invariant;
	}

}
