package de.prob.model.eventb

import de.prob.animator.domainobjects.EventB
import de.prob.model.representation.Set

class ContextModifier {
	private UUID uuid = UUID.randomUUID()
	private ctr = 0
	Context context

	def ContextModifier(Context context) {
		this.context = context
	}

	def ContextModifier enumerated_set(HashMap properties) {
		if (properties["name"] == null || properties["constants"] == null) {
			throw new IllegalArgumentException("must set properties 'name' and 'constants' for enumerated sets")
		}
		addEnumeratedSet(properties["name"], properties["constants"] as String[])
		this
	}

	/**
	 * Adds the set and constants to the context, as well as the generated partition axiom.
	 * @param setName of enumerated set to be added
	 * @param elements contained in the specified set
	 * @return the {@link EnumeratedSetBlock} generated when creating the set
	 */
	def EnumeratedSetBlock addEnumeratedSet(String setName, String... elements) {
		Set set = new Set(new EventB(setName))
		context.sets << set
		def constants = elements.collect {
			new EventBConstant(it, false, null)
		}
		context.constants.addAll(constants)
		def elementString = elements.collect { "{$it}" }.join(",")
		def axiom = "partition($setName,$elementString)"
		def typingAxiom = addAxiom(axiom)
		new EnumeratedSetBlock(set, constants, typingAxiom)
	}

	/**
	 * Removes an enumerated set, all of the elements contained in the set,
	 * and the typing axiom.
	 * @param block containing the elements to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeEnumeratedSet(EnumeratedSetBlock block) {
		def a = context.sets.remove(block.set)
		def bs = block.constants.collect {
			context.constants.remove(it)
		}
		def b = bs.inject(true, {x,y -> x && y})
		def c = removeAxiom(block.typingAxiom)
		return a && b && c
	}

	def ContextModifier set(String set) {
		addSet(set)
		this
	}

	/**
	 * Add a carrier set to a context
	 * @param set to be added
	 * @return {@link BSet} added to the {@link Context}
	 */
	def Set addSet(String set) {
		def bset = new Set(new EventB(set))
		context.sets << bset
		bset
	}

	/**
	 * Remove a set from a context
	 * @param set to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeSet(Set set) {
		return context.sets.remove(set)
	}

	def ContextModifier constant(String identifier) {
		addConstant(identifier)
		this
	}

	/**
	 * Add a constant to a context
	 * @param identifier to be added as a constant
	 * @return the {@link EventBConstant} added to the {@link Context}
	 */
	def EventBConstant addConstant(String identifier) {
		def constant = new EventBConstant(identifier, false, null)
		context.constants << constant
		constant
	}

	/**
	 * Remove a constant from the context
	 * @param constant to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeConstant(EventBConstant constant) {
		return context.constants.remove(constant)
	}

	def ContextModifier axiom(LinkedHashMap props, boolean theorem=false) {
		if (props.size() == 1) {
			def prop = props.collect { k,v -> [k, v]}[0]
			return axiom(prop[0], prop[1], theorem)
		}
		throw new IllegalArgumentException("Invalid invariant definition "+properties)
	}

	def ContextModifier axiom(String name, String pred, boolean theorem=false) {
		def axm = new EventBAxiom(name, pred, theorem, Collections.emptySet());
		context.allAxioms << axm
		context.axioms << axm
		this
	}


	/**
	 * Add an axiom to a context
	 * @param predicate to be added as an axiom
	 * @return {@link EventBAxiom} added to the {@link Context}
	 */
	def EventBAxiom addAxiom(String predicate) {
		def axiom = new EventBAxiom("gen-axiom-${uuid.toString()}-${ctr++}", predicate, false, Collections.emptySet())
		context.axioms << axiom
		context.allAxioms << axiom
		axiom
	}

	/**
	 * Remove an axiom from a context
	 * @param axiom to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeAxiom(EventBAxiom axiom) {
		def a = context.allAxioms.remove(axiom)
		def b = context.axioms.remove(axiom)
		if(a && b) {
			context.proofs.clear()
		}
		return a && b
	}

	def ContextModifier make(Closure definition) {
		runClosure definition
		this
	}

	private runClosure(Closure runClosure) {
		// Create clone of closure for threading access.
		Closure runClone = runClosure.clone()

		// Set delegate of closure to this builder.
		runClone.delegate = this

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()
	}
}
