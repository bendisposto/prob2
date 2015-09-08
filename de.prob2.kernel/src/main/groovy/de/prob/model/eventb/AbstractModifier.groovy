package de.prob.model.eventb

import org.eventb.core.ast.extension.IFormulaExtension

import de.be4.classicalb.core.parser.node.AIdentifierExpression
import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EvaluationException
import de.prob.animator.domainobjects.EventB



class AbstractModifier {

	public final Set<IFormulaExtension> typeEnvironment

	def AbstractModifier(Set<IFormulaExtension> typeEnvironment) {
		this.typeEnvironment = typeEnvironment
	}

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

	protected getDefinition(Map definition) {
		new Definition(definition)
	}

	def static int extractCounter(String prefix, List elements) {
		int counter = -1
		elements.each { e ->
			if (e.getName() ==~ "$prefix[0-9]+") {
				def str = e.getName().replace(prefix, "")
				int cnt = str as Integer
				if (cnt > counter) {
					counter = cnt
				}
			}
		}
		counter
	}

	def EventB parseIdentifier(String formula) throws ModelGenerationException {
		try {
			EventB f = new EventB(formula, typeEnvironment)
			if (!(f.getAst() instanceof AIdentifierExpression)) {
				throw new FormulaTypeException(f, "IDENTIFIER")
			}
			return f
		} catch(EvaluationException e) {
			throw new FormulaParseException(formula)
		}
	}

	def EventB parsePredicate(String formula) throws ModelGenerationException {
		return parseFormula(formula, EvalElementType.PREDICATE)
	}

	def EventB parseExpression(String formula) throws ModelGenerationException {
		return parseFormula(formula, EvalElementType.EXPRESSION)
	}

	def EventB parseFormula(String formula, EvalElementType expected) throws ModelGenerationException {
		try {
			EventB f = new EventB(formula, typeEnvironment)
			String kind = f.getKind()
			if (!kind.equals(expected.toString())) {
				throw new FormulaTypeException(f, expected.name())
			}
			return f
		} catch(EvaluationException e) {
			throw new FormulaParseException(formula)
		}
	}

	protected AbstractModifier runClosure(Closure runClosure) {
		// Create clone of closure for threading access.
		Closure runClone = runClosure.clone()

		def delegateH = new DelegateHelper(this)
		// Set delegate of closure to this builder.
		runClone.delegate = delegateH

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()

		delegateH.getState()
	}
}
