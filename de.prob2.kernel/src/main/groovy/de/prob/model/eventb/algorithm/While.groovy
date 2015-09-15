package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB

class While extends Statement {
	def EventB condition
	def EventB notCondition
	def EventB variant
	def Block block

	def While(String condition, String variant, Block block, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.condition = parsePredicate(condition)
		this.notCondition = parsePredicate("not($condition)")
		this.variant = variant ? parseExpression(variant) : null
		this.block = block
	}

	def String toString() {
		"while (${condition.toUnicode()}):"
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof While) {
			return this.condition.getCode().equals(that.getCondition().getCode()) &&
					this.block.equals(that.getBlock())
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.condition.hashCode() * 7 + this.block.hashCode() * 13;
	}
}
