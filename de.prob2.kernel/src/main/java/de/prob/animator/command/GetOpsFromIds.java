package de.prob.animator.command;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.GetOpFromId;
import de.prob.statespace.Transition;

public class GetOpsFromIds extends AbstractCommand {
	private final ComposedCommand allCommands;

	public GetOpsFromIds(final Collection<Transition> edges, final FormulaExpand expansion) {
		allCommands = new ComposedCommand(edges.stream()
			.filter(opInfo -> opInfo.canBeEvaluated(expansion))
			.map(opInfo -> new GetOpFromId(opInfo, expansion))
			.collect(Collectors.toList()));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		allCommands.writeCommand(pto);
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		allCommands.processResult(bindings);
	}

	@Override
	public List<AbstractCommand> getSubcommands() {
		return allCommands.getSubcommands();
	}
}
