package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.model.eventb.AbstractModifier
import de.prob.model.eventb.ModelGenerationException
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

	def Block If(String condition, Closure definition) throws ModelGenerationException {
		newBlock(statements.addElement(new If(condition, typeEnvironment).make(definition)))
	}

	def Block If(String condition, Block thenBlock, Block elseBlock) throws ModelGenerationException {
		newBlock(statements.addElement(new If(condition, typeEnvironment).Then(thenBlock).Else(elseBlock)))
	}

	def Block While(String condition, Closure definition) throws ModelGenerationException {
		newBlock(statements.addElement(new While(condition, null, newBlock().make(definition), typeEnvironment)))
	}

	def Block While(LinkedHashMap properties, String condition, Closure definition) throws ModelGenerationException {
		def props = validateProperties(properties, [variant: [String, null]])
		newBlock(statements.addElement(new While(condition, props.variant, newBlock().make(definition), typeEnvironment)))
	}

	def Block While(String condition, Block block, String variant=null) throws ModelGenerationException {
		newBlock(statements.addElement(new While(condition, variant, block, typeEnvironment)))
	}

	def Block Assert(String condition) throws ModelGenerationException {
		newBlock(statements.addElement(new Assertion(condition, typeEnvironment)))
	}

	def Block Assign(String... assignments) throws ModelGenerationException {
		def last = !statements.isEmpty() && statements[statements.size()-1] ? statements[statements.size()-1] : null
		def stmts = statements
		Assignments a = new Assignments([], typeEnvironment)
		if (last instanceof Assignments) {
			a = last
			stmts = stmts.removeElement(last)
		}

		newBlock(stmts.addMultiple(a.addAssignments(assignments)))
	}

	def Block make(Closure definition) throws ModelGenerationException {
		runClosure definition
	}
}
