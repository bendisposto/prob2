package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.representation.Set
import de.prob.model.representation.ModelElementList

class ContextModifierTest extends Specification {

	Context context
	ContextModifier modifier
	EventBModel model

	def setup() {
		model = new EventBModel(null)
		context = new Context("MyContext", "/")
		context.addAxioms(new ModelElementList<EventBAxiom>(), new ModelElementList<EventBAxiom>())
		context.addConstants(new ModelElementList<EventBConstant>())
		context.addExtends(new ModelElementList<Context>())
		context.addProofs(new ModelElementList<ProofObligation>())
		context.addSets(new ModelElementList<Set>())

		modifier = new ContextModifier(context)
	}

	def "it is possible to add an enumerated set"() {
		when:
		EnumeratedSetBlock block = modifier.addEnumeratedSet("set1", "x","y","z")

		then:
		context.sets.contains(block.set) &&
				context.constants.size() == 3 &&
				context.constants.contains(block.constants[0])&&
				context.constants.contains(block.constants[1])&&
				context.constants.contains(block.constants[2]) &&
				context.axioms.contains(block.typingAxiom)
	}

	def "it is possible to remove set elements once added"() {
		when:
		EnumeratedSetBlock block = modifier.addEnumeratedSet("set1", "x","y","z")
		def removed = modifier.removeEnumeratedSet(block)

		then:
		removed
	}

	def "it is possible to remove set elements after a deep copy"() {
		when:
		EnumeratedSetBlock block = modifier.addEnumeratedSet("set1", "x","y","z")
		def context2 = ModelModifier.deepCopy(model, context)
		def contained1 = context2.sets.contains(block.set)
		def contained2 = block.constants.collect { context2.constants.contains(it) }.inject(true, {a,b -> a &&b})
		def contained3 = context2.axioms.contains(block.typingAxiom)
		def mod2 = new ContextModifier(context2)
		def removed = mod2.removeEnumeratedSet(block)

		then:
		contained1 && contained2 && contained3 && removed
	}

	def "it is possible to add a carrier set"() {
		when:
		Set set = modifier.addSet("blah")

		then:
		context.sets.contains(set)
	}

	def "it is possible to remove a set once added"() {
		when:
		Set set = modifier.addSet("blah")
		def removed = modifier.removeSet(set)

		then:
		removed
	}

	def "it is possible to remove a set after a deep copy"() {
		when:
		Set set = modifier.addSet("blah")
		def context2 = ModelModifier.deepCopy(model, context)
		def contained = context2.sets.contains(set)
		def mod2 = new ContextModifier(context2)
		def removed = mod2.removeSet(set)

		then:
		contained && removed
	}

	def "it is possible to add an axiom"() {
		when:
		EventBAxiom axiom = modifier.addAxiom("TRUE = TRUE")

		then:
		context.axioms.contains(axiom)
	}

	def "it is possible to remove an axiom once added"() {
		when:
		EventBAxiom axiom = modifier.addAxiom("TRUE = TRUE")
		def removed = modifier.removeAxiom(axiom)

		then:
		removed
	}

	def "it is possible to remove an axiom after a deep copy"() {
		when:
		EventBAxiom axiom = modifier.addAxiom("TRUE = TRUE")
		def context2 = ModelModifier.deepCopy(model, context)
		def contained = context2.axioms.contains(axiom)
		def mod2 = new ContextModifier(context2)
		def removed = mod2.removeAxiom(axiom)

		then:
		contained && removed
	}

	def "it is possible to add a constant"() {
		when:
		EventBConstant constant = modifier.addConstant("x")

		then:
		context.constants.contains(constant)
	}

	def "it is possible to remove a constant"() {
		when:
		EventBConstant constant = modifier.addConstant("x")
		def removed = modifier.removeConstant(constant)

		then:
		removed
	}

	def "it is possible to remove a constant after a deep copy"() {
		when:
		EventBConstant constant = modifier.addConstant("x")
		def context2 = ModelModifier.deepCopy(model, context)
		def contained = context2.constants.contains(constant)
		def mod2 = new ContextModifier(context2)
		def removed = mod2.removeConstant(constant)

		then:
		contained && removed
	}
}
