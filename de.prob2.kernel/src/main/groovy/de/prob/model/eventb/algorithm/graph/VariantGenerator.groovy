package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.AlgorithmASTVisitor;
import de.prob.model.eventb.algorithm.ast.Assertion;
import de.prob.model.eventb.algorithm.ast.Assignment;
import de.prob.model.eventb.algorithm.ast.Block;
import de.prob.model.eventb.algorithm.ast.Call;
import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Return;
import de.prob.model.eventb.algorithm.ast.Skip;
import de.prob.model.eventb.algorithm.ast.While;

class VariantGenerator extends AlgorithmASTVisitor {

	def NodeNaming naming

	def VariantGenerator(NodeNaming naming) {
		this.naming = naming
	}

	@Override
	public visit(Assertion a) {
	}

	@Override
	public visit(Assignment a) {
	}

	@Override
	public visit(Block block) {
		block.statements.collect { visit(it) }
		.findAll()
	}

	@Override
	public visit(Call a) {
	}

	@Override
	public visit(If i) {
		def v1 = visit(i.Then)
		def v2 = visit(i.Else)
		(v1 + v2).iterator().join(" + ")
	}

	@Override
	public visit(Return a) {
	}

	@Override
	public visit(Skip a) {
	}

	@Override
	public visit(While w) {
		def v1 = null
		def v2 = visit(w.block)
		if (w.variant) {
			v1 = naming.getName(w) + "_variant"
			if (v2 && v2.size() == 1) {
				return v1 + " * "+v2[0]+" + "+v1
			}
			if (v2) {
				return v1 + " * ("+v2.iterator().join(" + ")+") + "+v1
			}
			return v1
		}
		return v2.iterator().join(" + ")
	}
}
