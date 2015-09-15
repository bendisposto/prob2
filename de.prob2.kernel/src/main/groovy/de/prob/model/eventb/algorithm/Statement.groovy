package de.prob.model.eventb.algorithm;

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.AbstractModifier

public abstract class Statement extends AbstractModifier {

	def Statement(Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
	}

	protected Block newBlock(List<Statement> statements=[]) {
		return new Block(statements, typeEnvironment)
	}
}
