package de.prob.model.eventb.algorithm.graph

class GraphOptimizer {

	INode algorithm
	Set<INode> seen = new HashSet<INode>()
	Set<INode> toUpdate = new HashSet<INode>()
	Map<INode, INode> translation = new HashMap<INode, INode>()

	def GraphOptimizer(INode algo) {
		algorithm = optimize(algo)
		seen = new HashSet<INode>()
		update(algorithm)
	}

	def INode check(INode node) {
		if (translation.containsKey(node)) {
			return translation[node]
		}
		if (seen.contains(node)) {
			toUpdate.add(node)
			return node
		}
		INode n = optimize(node)
		translation[node] = n
		n
	}

	def INode optimize(Node node) {
		seen.add(node)
		Node n = new Node(node.getStatements(), check(node.getOutNode()))
		translation[node] = n
		n
	}

	def INode optimize(Branch node) {
		seen.add(node)
		CombinedBranch optimized = optimizeBranches(node)
		translation[node] = optimized
		optimized
	}

	def CombinedBranch optimizeBranches(Branch node) {
		List<BranchCondition> branches = []
		if (node.getYesNode() instanceof Branch) {
			CombinedBranch branch = optimizeBranches(node.getYesNode())
			branch.branches.each { BranchCondition b ->
				def conditions = [node.getCondition()]
				def statements = [node.getStatement()]
				conditions.addAll(b.getConditions())
				statements.addAll(b.getStatements())
				branches.add(new BranchCondition(conditions, statements, b.getOutNode()))
			}
		} else {
			branches.add(new BranchCondition([node.getCondition()], [node.getStatement()], check(node.getYesNode())))
		}
		if (node.getNoNode() instanceof Branch) {
			CombinedBranch branch = optimizeBranches(node.getNoNode())
			branch.branches.each { BranchCondition b ->
				def conditions = [node.getNotCondition()]
				def statements = [node.getStatement()]
				conditions.addAll(b.getConditions())
				statements.addAll(b.getStatements())
				branches.add(new BranchCondition(conditions, statements, b.getOutNode()))
			}
		} else {
			branches.add(new BranchCondition([node.getNotCondition()], [node.getStatement()], check(node.getNoNode())))
		}
		return new CombinedBranch(branches)
	}

	def INode optimize(Nil node) {
		seen.add(node)
		translation[node] = node
		return node
	}

	def INode optimize(Graft node) {
		seen.add(node)
		INode outNode = check(node.getOutNode()) // eliminate Grafts to decrease number of events
		translation[node] = outNode
		outNode
	}

	def void update(Node node) {
		if (seen.contains(node)) {
			return
		}
		if (toUpdate.contains(node.getOutNode())) {
			node.setEndNode(translation.get(node.getOutNode()))
		}
		seen.add(node)
		update(node.getOutNode())
	}

	def void update(Branch node) {
		if (seen.contains(node)) {
			return
		}
		if (toUpdate.contains(node.getYesNode())) {
			node.setYesNode(translation.get(node.getYesNode()))
		}
		if (toUpdate.contains(node.getNoNode())) {
			node.setNoNode(translation.get(node.getNoNode()))
		}
		seen.add(node)
		update(node.getYesNode())
		update(node.getNoNode())
	}

	def void update(CombinedBranch node) {
		if (seen.contains(node)) {
			return
		}
		node.branches.each { BranchCondition b ->
			if (toUpdate.contains(b.getOutNode())) {
				b.setEndNode(translation.get(b.getOutNode()))
			}
		}
		seen.add(node)
		node.branches.each { BranchCondition b ->
			INode outNode = b.getOutNode()
			update(outNode)
		}
	}

	def void update(Nil node) {
		seen.add(node)
	}

	def void update(Graft node) {
		if (seen.contains(node)) {
			return
		}
		if (toUpdate.contains(node.getOutNode())) {
			node.setEndNode(translation.get(node.getOutNode()))
		}
		seen.add(node)
		update(node.getOutNode())
	}
}
