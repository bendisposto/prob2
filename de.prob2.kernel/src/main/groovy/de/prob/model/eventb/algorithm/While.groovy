package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException;

class While extends Statement {
	def EventB condition
	def EventB notCondition
	def EventB variant
	def Block block

	def While(String condition, String variant, Block block, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
		super(typeEnvironment)
		this.condition = parsePredicate(condition)
		this.notCondition = parsePredicate("not($condition)")
		this.variant = variant ? parseExpression(variant) : null
		this.block = block
	}

	private While(EventB condition, EventB notCondition, EventB variant, Block block, Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
		this.condition = condition
		this.notCondition = notCondition
		this.variant = variant
		this.block = block
	}

	def String toString() {
		"while (${condition.toUnicode()}):"
	}

	public While updateBlock(Block newBlock) {
		return new While(condition, notCondition, variant, newBlock, typeEnvironment)
	}
}
