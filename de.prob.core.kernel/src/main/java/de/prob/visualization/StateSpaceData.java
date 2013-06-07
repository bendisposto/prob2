package de.prob.visualization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;

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
	private final Transformer invOK;
	private final Transformer invKO;
	private final List<String> toInvOk = new ArrayList<String>();
	private final List<String> toInvKo = new ArrayList<String>();

	public StateSpaceData(final StateSpace s) {
		super();
		this.s = s;
		vars = extractVariables(s);
		for (IEvalElement e : vars) {
			s.subscribe(this, e);
		}
		invOK = new Transformer("").set("fill", "#799C79");
		invKO = new Transformer("").set("fill", "#B56C6C");
		addStyling(new Transformer("#sroot").set("width", "30px").set("height",
				"20px"));
		addStyling(new Transformer("#stroot").set("dx", "15px").set("dy",
				"13px"));
		mode = 1;
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
		Map<IEvalElement, EvaluationResult> values = s.getValues().get(id);
		if (values != null) {
			for (IEvalElement var : vars) {
				EvaluationResult res = values.get(var);
				if (res != null) {
					vs.add(var.getCode() + "=" + res.getValue());
				}
			}
		}
		if (id.equals(s.getRoot())) {
			vs.add("root");
		}

		Object inv = calculateInvariant(s, id);

		Node node = new Node(id.getId(), id.getId(), parentIndex, vs, inv);
		nodes.put(id.getId(), node);
		data.nodes.add(node);
		count++;
		return node;
	}

	private Object calculateInvariant(final StateSpace s, final StateId id) {
		Set<StateId> invariantOk = s.getInvariantOk();
		HashSet<StateId> invariantKo = s.getInvariantKo();
		if (invariantOk.contains(id)) {
			toInvOk.add("#s" + id.getId());
			return true;
		}
		if (invariantKo.contains(id)) {
			toInvKo.add("#s" + id.getId());
			return false;
		}

		return "unknown";
	}

	@Override
	public void addNewLinks(final StateSpaceGraph graph,
			final List<? extends OpInfo> newOps) {
		HashSet<StateId> ids = new HashSet<StateId>();
		for (OpInfo newOp : newOps) {
			ids.add(s.getVertex(newOp.getSrc()));
			ids.add(s.getVertex(newOp.getDest()));
		}
		s.evaluateForGivenStates(ids, vars);
		s.checkInvariants();

		List<OpInfo> ops = new ArrayList<OpInfo>();
		ops.addAll(newOps);
		s.execute(new GetOpsFromIds(ops));
		for (OpInfo opInfo : ops) {
			calculateInvariant(s, s.getVertex(opInfo.src));
			calculateInvariant(s, s.getVertex(opInfo.dest));
		}
		updateTransformers();
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

	@Override
	public void updateTransformers() {
		invOK.updateSelector(Joiner.on(",").join(toInvOk));
		invKO.updateSelector(Joiner.on(",").join(toInvKo));
		if (!toInvOk.isEmpty() && !data.styling.contains(invOK)) {
			addStyling(invOK);
			addStyling(new Transformer("#sroot").set("fill", "#000"));
			addStyling(new Transformer("#stroot").set("fill", "#fff")
					.set("stroke", "#fff").set("font-size", "12px")
					.set("stroke-width", "1px"));
		}
		if (!toInvKo.isEmpty() && !data.styling.contains(invKO)) {
			addStyling(invKO);
		}
	}

	@Override
	public void closeData() {
		for (IEvalElement e : vars) {
			s.unsubscribe(this, e);
		}
	}

}
