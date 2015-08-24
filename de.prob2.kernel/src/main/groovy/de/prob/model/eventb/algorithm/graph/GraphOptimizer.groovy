package de.prob.model.eventb.algorithm.graph

class GraphOptimizer {

	INode algorithm
	Map<INode, INode> translation = new HashMap<INode, INode>()

	def GraphOptimizer(INode algo) {
		algorithm = removeEmptyStatements(algo)
	}

	def INode check(INode node) {
		if (translation.containsKey(node)) {
			return translation[node]
		}
		INode n = removeEmptyStatements(node)
		translation[node] = n
		n
	}

	def INode removeEmptyStatements(Node node) {
		if (node.getStatements().isEmpty()) {
			return node.getOutNode()
		}
		return new Node(node.getStatements(), check(node.getOutNode()))
	}

	def INode removeEmptyStatements(Branch node) {
		INode y = check(node.yesNode)
		INode n = check(node.noNode)

		return new Branch(node.statement, y, n)
	}

	def INode removeEmptyStatements(Nil node) {
		return node
	}

	def INode removeEmptyStatements(Graft node) {
		return node
	}
}
