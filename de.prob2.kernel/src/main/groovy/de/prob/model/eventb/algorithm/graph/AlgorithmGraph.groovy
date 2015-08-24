package de.prob.model.eventb.algorithm.graph;



public class AlgorithmGraph {

	Map<Integer, INode> nodes = new HashMap<Integer, INode>()
	private Map<INode, Integer> nodeToPcMapping = new HashMap<INode, Integer>()
	Map<Integer, Set<Edge>> edges = new HashMap<Integer, Set<Edge>>()
	int pc

	def AlgorithmGraph(INode node) {
		pc = 0
		addNode(node)
	}

	def int getPC() {
		pc++
	}

	def int addNode(INode node) {
		if (nodeToPcMapping.containsKey(node)) {
			return nodeToPcMapping.get(node)
		}
		def pc = getPC()
		nodes[pc] = node
		nodeToPcMapping[node] = pc
		addEdges(pc, node)
		pc
	}

	def addEdge(int from, int to, String rep) {
		if (!edges.containsKey(from)) {
			edges.put(from, new HashSet<Edge>())
		}
		edges.get(from).add(new Edge(from, to, rep))
	}

	def addEdges(int pc, Nil node) {
	}

	def addEdges(int pc, Node node) {
		int topc = addNode(node.getOutNode())
		addEdge(pc, topc, "-->")
	}

	def addEdges(int pc, Branch node) {
		int yPc = addNode(node.getYesNode())
		addEdge(pc, yPc, "-- ${node.condition} -->")
		int nPc = addNode(node.getNoNode())
		addEdge(pc, nPc, "-- ${node.notCondition} -->")
	}

	def addEdges(int pc, CombinedBranch node) {
		node.branches.each { BranchCondition cond ->
			int newPc = addNode(cond.getOutNode())
			addEdge(pc, newPc, "--${cond.getConditions().toString()}-->")
		}
	}

	def addEdges(int pc, Graft node) {
		int topc = addNode(node.getOutNode())
		addEdge(pc, topc, "-->")
	}

	@Override
	public String toString() {
		return "Nodes: $nodes \n Edges: $edges"
	}
}
