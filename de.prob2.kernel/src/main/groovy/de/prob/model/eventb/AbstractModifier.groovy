package de.prob.model.eventb

import de.be4.classicalb.core.parser.node.*

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EvaluationException
import de.prob.animator.domainobjects.EventB
import de.prob.model.representation.AbstractElement

import org.eventb.core.ast.extension.IFormulaExtension

class AbstractModifier extends AbstractElement {

	public final Set<IFormulaExtension> typeEnvironment

	def AbstractModifier(Set<IFormulaExtension> typeEnvironment) {
		this.typeEnvironment = typeEnvironment ?: Collections.emptySet()
	}

	protected Map validateProperties(Map properties, Map required) {
		validate("properties", properties)
		required.collectEntries { String prop,type ->
			if (type instanceof List && type.size() == 2) {
				return validateOptionalProperty(properties, prop, type)
			}
			if (type instanceof EvalElementType) {
				return validateFormula(properties, prop, type)
			}
			if (type instanceof Class) {
				return validateRequiredProperty(properties, prop, type)
			}
			throw new IllegalArgumentException("incorrect properties: values must be either a class or a tuple with two elements")
		}
	}

	protected validateOptionalProperty(LinkedHashMap properties, String property, List type) {
		if (type.size() != 2) {
			throw new IllegalArgumentException("type tuple must contain two elements")
		}
		if (properties[property]) {
			return [
				property,
				properties[property].asType(type[0])
			]
		} else {
			return [property, type[1]]
		}
	}

	protected validateFormula(LinkedHashMap properties, String property, EvalElementType formulaType) {
		if (properties[property]) {
			return [
				property,
				parseFormula(properties[property], formulaType)
			]
		} else {
			throw new IllegalArgumentException("Could not find required property $property")
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

	protected Definition getDefinition(Map definition) {
		new Definition(definition)
	}

	def String getUniqueName(String name, List elements) {
		def element = elements.find { it.getName() == name }
		if (!element) {
			return name
		}

		def pre = name
		def counter = 0
		if ((name =~ "[0-9]+\$").find()) {
			pre = name.replaceAll("[0-9]+\$","")
			counter = name.replace(pre, "") as Integer
		}
		elements.each { e ->
			if (e.getName() ==~ "$pre[0-9]+") {
				def str = e.getName().replace(pre, "")
				int cnt = str as Integer
				if (cnt >= counter) {
					counter = cnt + 1
				}
			}
		}
		return pre + counter
	}

	def EventB parseIdentifier(String formula) throws ModelGenerationException {
		if (formula == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}
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

	def EventB parseAssignment(String formula) throws ModelGenerationException {
		return parseFormula(formula, EvalElementType.ASSIGNMENT)
	}

	def EventB parseFormula(String formula, EvalElementType expected) throws ModelGenerationException {
		if (formula == null) {
			throw new IllegalArgumentException("${expected.name().toLowerCase()} must not be null");
		}
		try {
			EventB f = new EventB(formula, typeEnvironment)
			ensureType(f, expected)
			return f
		} catch(EvaluationException e) {
			throw new FormulaParseException(formula)
		}
	}

	def EventB ensureType(EventB formula, EvalElementType expected) {
		if (formula.kind != expected) {
			throw new FormulaTypeException(formula, expected.name())
		}
		return formula
	}

	def validateAll(Object... es) {
		es.each {
			if (it == null) {
				throw new IllegalArgumentException("Expected argument to be an object but was null")
			}
		}
	}

	def Object validate(String name, Object e) {
		if (e == null) {
			throw new IllegalArgumentException("Argument $name must not be null")
		}
		return e
	}

	protected AbstractModifier runClosure(Closure runClosure) {
		validate('runClosure', runClosure)

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
