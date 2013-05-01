package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

public class GetOpsFromIds extends AbstractCommand {
	List<OpInfo> toCheck = new ArrayList<OpInfo>();
	List<GetOpFromId> cmds = new ArrayList<GetOpFromId>();
	ComposedCommand allCommands;

	public GetOpsFromIds(final Collection<OpInfo> edges) {
		for (OpInfo opInfo : edges) {
			if (!opInfo.isEvaluated()) {
				toCheck.add(opInfo);
				cmds.add(new GetOpFromId(opInfo.id));
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

		for (OpInfo op : toCheck) {
			GetOpFromId cmd = cmds.get(toCheck.indexOf(op));
			op.setInfo(cmd.getName(), cmd.getParams(), cmd.getTargetState());
		}
	}

	@Override
	public List<AbstractCommand> getSubcommands() {
		return allCommands.getSubcommands();
	}

}
