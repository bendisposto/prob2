package de.prob.model.eventb

import de.prob.animator.domainobjects.EventB
import de.prob.model.representation.Axiom
import de.prob.model.representation.Constant
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Set

class ContextModifier extends AbstractModifier {
	private final axmctr
	final Context context

	def ContextModifier(Context context, List<Context> extended=[]) {
		this(context, 0)
	}

	def ContextModifier(Context context, int axmctr) {
		this.context = context
		this.axmctr = axmctr
	}

	def ContextModifier newCM(Context context) {
		return new ContextModifier(context, axmctr)
	}

	def ContextModifier addExtends(List<Context> extendBlock) {
		newCM(context.set(Context.class, new ModelElementList<Context>(extendBlock)))
	}


	def ContextModifier enumerated_set(HashMap properties) {
		Map validated = validateProperties(properties, [name: String, constants: String[]])
		addEnumeratedSet(validated["name"], validated["constants"])
	}

	/**
	 * Adds the set and constants to the context, as well as the generated partition axiom.
	 * @param setName of enumerated set to be added
	 * @param elements contained in the specified set
	 * @return the {@link EnumeratedSetBlock} generated when creating the set
	 */
	def ContextModifier addEnumeratedSet(String setName, String... elements) {
		ContextModifier cm = set(setName)
		elements.each {
			cm = cm.constant(it)
		}
		def elementString = elements.collect { "{$it}" }.join(",")
		cm.axiom("partition($setName,$elementString)")
	}

	def ContextModifier set(String set) {
		def bset = new Set(new EventB(set))
		new ContextModifier(context.addTo(Set.class, bset),axmctr)
	}

	/**
	 * Remove a set from a context
	 * @param set to be removed
	 */
	def ContextModifier removeSet(Set set) {
		return newCM(context.removeFrom(Set.class, set))
	}

	def ContextModifier constants(String... constants) {
		ContextModifier cm = this
		constants.each {
			cm = cm.constant(it)
		}
		cm
	}

	def ContextModifier constant(String identifier) {
		newCM(context.addTo(Constant.class, new EventBConstant(identifier, false, null)))
	}

	/**
	 * Remove a constant from the context
	 * @param constant to be removed
	 */
	def ContextModifier removeConstant(EventBConstant constant) {
		return newCM(context.removeFrom(Constant.class, constant))
	}

	def ContextModifier axioms(Map axioms) {
		ContextModifier cm = this
		axioms.each { k,v ->
			cm = cm.axiom(k,v)
		}
		cm
	}

	def ContextModifier axioms(String... axioms) {
		ContextModifier cm = this
		axioms.each {
			cm = cm.axiom(it)
		}
		cm
	}

	def ContextModifier theorem(String thm) {
		axiom(thm, true)
	}

	def ContextModifier theorem(Map props) {
		axiom(props, true)
	}

	def ContextModifier axiom(Map props, boolean theorem=false) {
		Definition prop = getDefinition(props)
		return axiom(prop.label, prop.formula, theorem)
	}

	def ContextModifier axiom(String pred, boolean theorem=false) {
		int ctr = axmctr + 1
		new ContextModifier(axiom("a$ctr", pred, theorem).getContext(),ctr)
	}

	def ContextModifier axiom(String name, String predicate, boolean theorem=false) {
		def axiom = new EventBAxiom(name, predicate, theorem, Collections.emptySet())
		newCM(context.addTo(Axiom.class, axiom))
	}

	/**
	 * Remove an axiom from a context
	 * @param axiom to be removed
	 * @return whether or not the removal was successful
	 */
	def ContextModifier removeAxiom(EventBAxiom axiom) {
		return newCM(context.removeFrom(Axiom.class, axiom))
	}

	def ContextModifier make(Closure definition) {
		runClosure definition
	}

}
