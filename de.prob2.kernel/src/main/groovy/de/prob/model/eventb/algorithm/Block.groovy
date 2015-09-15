package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.model.eventb.AbstractModifier
import de.prob.model.representation.ModelElementList


class Block extends AbstractModifier {
	final ModelElementList<Statement> statements

	def Block(List<Statement> statements=[], Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.statements = new ModelElementList<Statement>(statements)
	}

	private Block newBlock(List<Statement> statements=[]) {
		return new Block(statements, typeEnvironment)
	}

	def Block If(String condition, Closure definition) {
		newBlock(statements.addElement(new If(condition, typeEnvironment).make(definition)))
	}

	def Block While(String condition, Closure definition) {
		newBlock(statements.addElement(new While(condition, null, newBlock().make(definition), typeEnvironment)))
	}

	def Block While(LinkedHashMap properties, String condition, Closure definition) {
		def props = validateProperties(properties, [variant: [String, null]])
		newBlock(statements.addElement(new While(condition, props.variant, newBlock().make(definition), typeEnvironment)))
	}

	def Block Assert(String condition) {
		newBlock(statements.addElement(new Assertion(condition, typeEnvironment)))
	}

	def Block Assign(String... assignments) {
		newBlock(statements.addElement(new Assignments(assignments as List, typeEnvironment)))
	}

	def Block make(Closure definition) {
		runClosure definition
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Block) {
			return this.statements.equals(that.getStatements())
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.statements.hashCode() * 7
	}
}
