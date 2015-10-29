package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException;

class While extends Statement {
	def EventB condition
	def EventB notCondition
	def EventB variant
	def EventB invariant
	def Block block

	def While(String condition, String variant, String invariant, Block block, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
		super(typeEnvironment)
		this.condition = parsePredicate(condition)
		this.notCondition = parsePredicate("not($condition)")
		this.variant = variant ? parseExpression(variant) : null
		this.invariant = invariant ? parsePredicate(invariant) : null
		this.block = block
	}

	private While(EventB condition, EventB notCondition, EventB variant, EventB invariant, Block block, Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
		this.condition = condition
		this.notCondition = notCondition
		this.variant = variant
		this.invariant = invariant
		this.block = block
	}

	def String toString() {
		"while (${condition.toUnicode()}):"
	}

	public While updateBlock(Block newBlock) {
		return new While(condition, notCondition, variant, invariant, newBlock, typeEnvironment)
	}
}
