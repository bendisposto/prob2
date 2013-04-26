package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.prob.animator.command.GetOpsFromIds;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.StateSpaceGraph;

public class StateSpaceData extends AbstractData {

	private final StateSpace s;
	private final List<IEvalElement> vars;

	public StateSpaceData(final StateSpace s) {
		super();
		this.s = s;
		vars = extractVariables(s);
	}

	private List<IEvalElement> extractVariables(final StateSpace s) {
		List<IEvalElement> vars = new ArrayList<IEvalElement>();
		Set<Machine> ms = s.getModel().getChildrenOfType(Machine.class);
		for (Machine machine : ms) {
			Set<Variable> vs = machine.getChildrenOfType(Variable.class);
			for (Variable variable : vs) {
				vars.add(variable.getEvaluate());
			}
		}
		return vars;
	}

	@Override
	protected Node addNode(final StateId id, final int parentIndex) {
		List<String> vs = new ArrayList<String>();
		Map<IEvalElement, EvaluationResult> valuesAt = s.valuesAt(id);
		for (IEvalElement var : vars) {
			EvaluationResult res = valuesAt.get(var);
			if (res != null) {
				vs.add(var.getCode() + "=" + res.getValue());
			}
		}

		Node node = new Node(id.getId(), id.getId(), parentIndex, vs);
		nodes.put(id.getId(), node);
		data.nodes.add(node);
		count++;
		return node;
	}

	@Override
	public void addNewLinks(final StateSpaceGraph graph,
			final List<? extends OpInfo> newOps) {
		List<OpInfo> ops = new ArrayList<OpInfo>();
		ops.addAll(newOps);
		s.execute(new GetOpsFromIds(ops));
		super.addNewLinks(graph, newOps);
	}

	@Override
	public Node addNode(final StateId id) {
		return addNode(id, -1);
	}

	@Override
	public Link addLink(final OpInfo op) {
		Node src = nodes.get(op.src);
		Node dest = nodes.get(op.dest);
		Link link = new Link(op.id, data.nodes.indexOf(src),
				data.nodes.indexOf(dest), op.getRep(s.getModel()), "#666");
		links.put(op.id, link);
		data.links.add(link);
		count++;
		return link;
	}

	@Override
	public int varSize() {
		return vars.size();
	}

}
