package de.prob.model.eventb.algorithm.ast

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
		newBlock(statements.addElement(new While(condition, null, null, newBlock().make(definition), typeEnvironment)))
	}

	def Block While(LinkedHashMap properties, String condition, Closure definition) throws ModelGenerationException {
		def props = validateProperties(properties, [variant: [String, null], invariant: [String, null]])
		newBlock(statements.addElement(new While(condition, props.variant, props.invariant, newBlock().make(definition), typeEnvironment)))
	}

	def Block While(String condition, Block block, String invariant=null, String variant=null) throws ModelGenerationException {
		newBlock(statements.addElement(new While(condition, variant, invariant, block, typeEnvironment)))
	}

	def Block Assert(String condition) throws ModelGenerationException {
		newBlock(statements.addElement(new Assertion(condition, typeEnvironment)))
	}

	def Block Assume(String condition) throws ModelGenerationException {
		newBlock(statements.addElement(new Assumption(condition, typeEnvironment)))
	}

	def Block Assign(String assignment) throws ModelGenerationException {
		Assignment a = new Assignment(assignment, typeEnvironment)
		newBlock(statements.addElement(a))
	}

	def Block Return(String... returnVals) throws ModelGenerationException {
		Return(returnVals as List)
	}

	def Block Return(List<String> returnVals) throws ModelGenerationException  {
		newBlock(statements.addElement(new Return(returnVals, typeEnvironment)))
	}

	def Block Call(String name, List<String> arguments, List<String> results) throws ModelGenerationException{
		newBlock(statements.addElement(new Call(name, arguments, results, typeEnvironment)))
	}

	def Block make(Closure definition) throws ModelGenerationException {
		runClosure definition
	}
}
