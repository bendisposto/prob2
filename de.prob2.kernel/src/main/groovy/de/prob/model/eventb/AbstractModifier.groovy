package de.prob.model.eventb

import org.codehaus.groovy.transform.tailrec.VariableReplacedListener.*
import org.eventb.core.ast.extension.IFormulaExtension

import de.be4.classicalb.core.parser.node.AIdentifierExpression
import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EvaluationException
import de.prob.animator.domainobjects.EventB
import de.prob.model.representation.AbstractElement



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
		if (!formula.getKind().equals(expected.toString())) {
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
