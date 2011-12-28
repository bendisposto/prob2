package de.prob.model;

import java.util.List;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.OpInfo;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class StateSpace extends DirectedSparseMultigraph<String, Operation>
		implements IAnimator {

	private static final long serialVersionUID = -9047891508993732222L;
	private transient final IAnimator animator;
	private StateTemplate template;

	@Inject
	public StateSpace(final IAnimator animator) {
		this.animator = animator;
		addVertex("root");
	}

	public void exploreState(final String id) throws ProBException {
		ExploreStateCommand command = new ExploreStateCommand(id);
		animator.execute(command);
		List<OpInfo> enabledOperations = command.getEnabledOperations();
		// (id,name,src,dest,args)
		for (OpInfo ops : enabledOperations) {
			addEdge(new Operation(ops.id, ops.name, ops.params), ops.src,
					ops.dest, EdgeType.DIRECTED);
		}
	}

	@Override
	public void execute(final ICommand command) throws ProBException {
		animator.execute(command);
	}

	public void setStateTemplate(final StateTemplate template) {
		this.template = template;
	}
}
