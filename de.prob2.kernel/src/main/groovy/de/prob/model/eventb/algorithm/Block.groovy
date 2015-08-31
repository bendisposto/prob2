package de.prob.model.eventb.algorithm

import de.prob.model.eventb.DelegateHelper
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.ModelElementList


class Block extends AbstractElement {
	final ModelElementList<Statement> statements

	def Block(List<Statement> statements=[]) {
		this.statements = new ModelElementList<Statement>(statements)
	}

	def Block If(String condition, Closure definition) {
		new Block(statements.addElement(new If(condition).make(definition)))
	}

	def Block While(String condition, Closure definition) {
		new Block(statements.addElement(new While(condition, null, new Block().make(definition))))
	}

	def Block While(LinkedHashMap properties, String condition, Closure definition) {
		def props = validateProperties(properties, [variant: [String, null]])
		new Block(statements.addElement(new While(condition, props.variant, new Block().make(definition))))
	}

	def Block Assert(String condition) {
		new Block(statements.addElement(new Assertion(condition)))
	}

	def Block Assign(String... assignments) {
		new Block(statements.addElement(new Assignments(assignments as List)))
	}

	def Block make(Closure definition) {
		// Create clone of closure for threading access.
		Closure runClone = definition.clone()

		def delegateH = new DelegateHelper(this)
		// Set delegate of closure to this builder.
		runClone.delegate = delegateH

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()

		delegateH.getState()
	}

	// TODO consolidate these methods in a Util class
	protected Map validateProperties(Map properties, Map required) {
		required.collectEntries { String prop,type ->
			if (type instanceof List && type.size() == 2) {
				return validateOptionalProperty(properties, prop, type)
			}
			if (type instanceof Class) {
				return validateRequiredProperty(properties, prop, type)
			}
			throw new IllegalArgumentException("incorrect properties: values must be either a class or a tuple with two elements")
		}
	}

	protected validateOptionalProperty(LinkedHashMap properties, String property, List type) {
		if (properties[property]) {
			return [
				property,
				properties[property].asType(type[0])
			]
		} else {
			return [property, type[1]]
		}
	}

	protected validateRequiredProperty(LinkedHashMap properties, String property, Class type) {
		if (properties[property]) {
			return [
				property,
				properties[property].asType(type)
			]
		} else {
			throw new IllegalArgumentException("Expected property $property to have type $type")
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof While) {
			return this.statements.equals(that.getStatements())
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.statements.hashCode() * 7
	}
}
