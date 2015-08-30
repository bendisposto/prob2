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
		addEdge(pc, topc, null)
	}

	def addEdges(int pc, Branch node) {
		int yPc = addNode(node.getYesNode())
		addEdge(pc, yPc, new BranchCondition([node.condition], [node.statement], null))
		int nPc = addNode(node.getNoNode())
		addEdge(pc, nPc, new BranchCondition([node.notCondition], [node.statement], null))
	}

	def addEdges(int pc, CombinedBranch node) {
		node.branches.each { BranchCondition cond ->
			int newPc = addNode(cond.getOutNode())
			addEdge(pc, newPc, cond)
		}
	}

	def addEdges(int pc, Graft node) {
		int topc = addNode(node.getOutNode())
		addEdge(pc, topc, null)
	}

	def Set<Edge> getOutEdges(int nodeId) {
		return edges.get(nodeId)
	}

	def Set<Edge> getInEdges(int nodeId) {
		Set<Edge> edges = new HashSet<Edge>()
		edges.each { k, ed ->
			if (ed.to == nodeId) {
				edges.add(ed)
			}
		}
		edges
	}

	@Override
	public String toString() {
		return "Nodes: $nodes \n Edges: $edges"
	}

	public int size() {
		return pc
	}
}
