package de.prob.model.eventb

import de.prob.model.representation.Axiom
import de.prob.model.representation.BSet

class ContextModifier {
	private UUID uuid = UUID.randomUUID()
	private ctr = 0
	Context context

	def ContextModifier(Context context) {
		this.context = context
	}

	/**
	 * Adds the set and constants to the context, as well as the generated partition axiom.
	 * @param setName of enumerated set to be added
	 * @param elements contained in the specified set
	 * @return the {@link EnumeratedSetBlock} generated when creating the set
	 */
	def EnumeratedSetBlock addEnumeratedSet(String setName, String... elements) {
		BSet set = new BSet(setName)
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
		def c = context.axioms.remove(block.typingAxiom)
		context.getChildrenOfType(Axiom.class).remove(block.typingAxiom)
		return a && b && c
	}

	/**
	 * Add a carrier set to a context
	 * @param set to be added
	 * @return {@link BSet} added to the {@link Context}
	 */
	def BSet addSet(String set) {
		def bset = new BSet(set)
		context.sets << bset
		bset
	}

	/**
	 * Remove a set from a context
	 * @param set to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeSet(BSet set) {
		return context.sets.remove(set)
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

	/**
	 * Add an axiom to a context
	 * @param predicate to be added as an axiom
	 * @return {@link EventBAxiom} added to the {@link Context}
	 */
	def EventBAxiom addAxiom(String predicate) {
		def axiom = new EventBAxiom("gen-axiom-${uuid.toString()}-${ctr++}", predicate, false, Collections.emptySet())
		context.axioms << axiom
		context.getChildrenOfType(Axiom.class) << axiom
		axiom
	}

	/**
	 * Remove an axiom from a context
	 * @param axiom to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeAxiom(EventBAxiom axiom) {
		// TODO: remove all proof obligations in which this axiom might have been used
		def a = context.getChildrenOfType(Axiom.class).remove(axiom)
		def b = context.axioms.remove(axiom)
		return a && b
	}
}
