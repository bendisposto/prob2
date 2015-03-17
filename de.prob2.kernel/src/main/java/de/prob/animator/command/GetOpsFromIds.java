package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.GetOpFromId;
import de.prob.statespace.Transition;

public class GetOpsFromIds extends AbstractCommand {
	List<GetOpFromId> cmds = new ArrayList<GetOpFromId>();
	ComposedCommand allCommands;

	public GetOpsFromIds(final Collection<Transition> edges,
			final boolean truncate) {
		for (Transition opInfo : edges) {
			if (opInfo.canBeEvaluated(truncate)) {
				cmds.add(new GetOpFromId(opInfo, truncate));
			}
		}
		List<AbstractCommand> cs = new ArrayList<AbstractCommand>(cmds);
		allCommands = new ComposedCommand(cs);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		allCommands.writeCommand(pto);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		allCommands.processResult(bindings);
	}

	@Override
	public List<AbstractCommand> getSubcommands() {
		return allCommands.getSubcommands();
	}

}
