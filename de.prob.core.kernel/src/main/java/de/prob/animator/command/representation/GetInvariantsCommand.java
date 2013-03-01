package de.prob.animator.command.representation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.animator.command.ICommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.classicalb.PrettyPrinter;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Extracts the string representation of the invariants from ProB
 * 
 * @author joy
 * 
 */
public class GetInvariantsCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetInvariantsCommand.class);
	private static final String LIST = "LIST";
	List<ClassicalB> invariant;
	private final NodeIdAssignment nodeIdMapping;

	public GetInvariantsCommand(final NodeIdAssignment nodeIdMapping) {
		this.nodeIdMapping = nodeIdMapping;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_invariants").printVariable(LIST).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		final ArrayList<ClassicalB> r = new ArrayList<ClassicalB>();

		final PrologTerm prologTerm = bindings.get(LIST);
		final ListPrologTerm invs = BindingGenerator.getList(prologTerm);
		for (final PrologTerm invTerm : invs) {
			final CompoundPrologTerm inv = BindingGenerator.getCompoundTerm(
					invTerm, 2);
			final int id = Integer.parseInt(inv.getArgument(2).toString());
			// final String name = inv.getArgument(1).getFunctor();
			final Node ID = nodeIdMapping.lookupById(id);
			final String prettyPrint = prettyprint(ID);
			r.add(new ClassicalB(prettyPrint));
		}
		invariant = r;

	}

	public List<ClassicalB> getInvariants() {
		return invariant;
	}

	private String prettyprint(final Node predicate) {
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		predicate.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

}
