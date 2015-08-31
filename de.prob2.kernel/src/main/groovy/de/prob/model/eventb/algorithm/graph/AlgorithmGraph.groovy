package de.prob.model.eventb.algorithm.graph;


import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments



public class AlgorithmGraph {

	def List<EventInfo> nodes = new ArrayList<EventInfo>()
	def Map<Integer, Assertion> assertions = new HashMap<Integer, Assertion>()
	private Map<INode, EventInfo> nodeToInfoMapping = new HashMap<INode, EventInfo>()
	private Map<INode, Integer> nodeToPcMapping = new HashMap<INode, Integer>()
	int pc

	def AlgorithmGraph(INode node) {
		pc = 0
		int startpc = pc
		if (node instanceof Node) {
			addNode(node, false)
			getInfo(node).addEdge(startpc, new BranchCondition([], [], node))
		} else if (node instanceof Branch || node instanceof CombinedBranch || node instanceof AssertNode) {
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
		if (increasePC || node instanceof Branch || node instanceof CombinedBranch) {
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

	def addEdges(int pc, Nil node) {
	}

	def addEdges(int pc, Node node) {
		int topc = addNode(node.getOutNode())
		getInfo(node.getOutNode()).addEdge(topc, new BranchCondition([], [], node.getOutNode()))
		EventInfo info = getInfo(node)
		info.addActions(node.getStatements())
		info.addActions([
			new Assignments(["pc := $topc"])
		])
	}

	def addEdges(int pc, Branch node) {
		addNode(node.getYesNode(), false)
		getInfo(node.getYesNode()).addEdge(pc, new BranchCondition([node.condition], [node.statement], node.getYesNode()))
		addNode(node.getNoNode(), false)
		getInfo(node.getNoNode()).addEdge(pc, new BranchCondition([node.notCondition], [node.statement], node.getNoNode()))
	}

	def addEdges(int pc, CombinedBranch node) {
		node.branches.each { BranchCondition cond ->
			addNode(cond.getOutNode(), false)
			getInfo(cond.getOutNode()).addEdge(pc, cond)
		}
	}

	def addEdges(int pc, AssertNode node) {
		int topc = addNode(node.getOutNode(), false)
		getInfo(node.getOutNode()).addEdge(topc, new BranchCondition([], [], node.getOutNode()))
		assertions[topc] = node.getAssertion()
	}

	def addEdges(int pc, Graft node) {
		int topc = addNode(node.getOutNode())
		getInfo(node.getOutNode()).addEdge(topc, new BranchCondition([], [], node.getOutNode()))
		getInfo(node).addActions([
			new Assignments(["pc := $topc"])
		])
	}

	def outNode(INode node) {
		if (node.getOutNode() instanceof AssertNode) {
			return node.getOutNode().getOutNode()
		}
		return node.getOutNode()
	}

	@Override
	public String toString() {
		return nodes.toString()
	}

	public int size() {
		return nodes.size()
	}
}
