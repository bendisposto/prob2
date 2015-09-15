package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments



public class AlgorithmGraph {

	def List<EventInfo> nodes = new ArrayList<EventInfo>()
	def Map<Integer, List<Assertion>> assertions = new HashMap<Integer, List<Assertion>>()
	private Map<INode, EventInfo> nodeToInfoMapping = new HashMap<INode, EventInfo>()
	private Map<INode, Integer> nodeToPcMapping = new HashMap<INode, Integer>()
	int pc

	def AlgorithmGraph(INode node) {
		pc = 0
		int startpc = pc
		if (node instanceof Node) {
			addNode(node, false)
			getInfo(node).addEdge(startpc, new BranchCondition([], [], node))
			addAssertions(node, startpc)
		} else if (node instanceof Branch) {
			addNode(node, false)
		}
	}

	def int getPC(boolean increasePC) {
		if (increasePC) {
			pc++
		}
		pc
	}

	def EventInfo getInfo(INode node) {
		if (!nodeToInfoMapping.containsKey(node)) {
			def info = new EventInfo()
			nodeToInfoMapping.put(node, info)
		}
		return nodeToInfoMapping.get(node)
	}

	def int addNode(INode node, boolean increasePC=true) {
		if (nodeToPcMapping.containsKey(node)) {
			return nodeToPcMapping.get(node)
		}
		def pc = getPC(increasePC)

		if (increasePC || node instanceof Branch) {
			nodeToPcMapping[node] = pc
		}
		if (node instanceof Node || node instanceof Nil) {
			if (!nodes.contains(getInfo(node))) {
				nodes.add(getInfo(node))
			} else {
				return pc
			}
		}
		addEdges(pc, node)
		pc
	}

	def addAssertions(INode node, int pc) {
		assertions[pc] = assertions[pc] ? assertions[pc] : []
		node.getAssertions().each { Assertion a ->
			if (!assertions[pc].contains(a)) {
				assertions[pc] << a
			}
		}
	}

	def addEdges(int pc, Nil node) {
	}

	def addEdges(int pc, Node node) {
		int topc = addNode(node.getOutNode())
		getInfo(node.getOutNode()).addEdge(topc, new BranchCondition([], [], node.getOutNode()))
		addAssertions(node.getOutNode(), topc)
		EventInfo info = getInfo(node)
		info.addActions(node.getStatements())
		info.addActions([
			new Assignments(["pc := $topc"])
		])
	}

	def addEdges(int pc, Branch node) {
		addAssertions(node, pc)
		node.branches.each { BranchCondition cond ->
			if (cond.getOutNode().assertions) {
				assert !(cond.getOutNode() instanceof Nil)
				cond.getOutNode().assertions.each { Assertion assertion ->
					cond.getOutNode().getOutNode().addAssertion(assertion)
				}
			}
			addNode(cond.getOutNode(), false)
			getInfo(cond.getOutNode()).addEdge(pc, cond)
		}
	}

	@Override
	public String toString() {
		return nodes.toString()
	}

	public int size() {
		return nodes.size()
	}
}
