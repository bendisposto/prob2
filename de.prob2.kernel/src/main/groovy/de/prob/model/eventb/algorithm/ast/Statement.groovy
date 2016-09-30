package de.prob.model.eventb.algorithm.ast;

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.model.eventb.AbstractModifier

public abstract class Statement extends AbstractModifier {

	def Statement(Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
	}

	protected Block newBlock(List<Statement> statements=[]) {
		return new Block(statements, typeEnvironment)
	}
}
