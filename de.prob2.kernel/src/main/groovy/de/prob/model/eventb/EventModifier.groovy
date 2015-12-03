package de.prob.model.eventb

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.Action
import de.prob.model.representation.ElementComment
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class EventModifier extends AbstractModifier {
	def Event event
	boolean initialisation

	public EventModifier(Event event, boolean initialisation=false, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.initialisation = initialisation
		this.event = event
	}

	private EventModifier newEM(Event event) {
		return new EventModifier(event, initialisation, typeEnvironment)
	}

	def EventModifier refines(Event refinedEvent, boolean extended) {
		validate('refinedEvent', refinedEvent)
		Event e = event.toggleExtended(extended)
		e = e.set(Event.class, new ModelElementList<Event>([refinedEvent]))
		if (extended) {
			def actions = refinedEvent.getAllActions()
			if (e.actions) {
				def acts = e.actions.inject([]) { List<EventBAction> added, EventBAction a ->
					def newName = getUniqueName(a.getName(), actions + added)
					def newA = newName == a.getName() ? a : new EventBAction(newName, a.getCode(), a.getComment())
					added << newA
				}
				e = e.set(Action.class, new ModelElementList<EventBAction>(acts))
			}
			def guards = refinedEvent.getAllGuards()
			if (e.guards) {
				def grds = e.guards.inject([]) { List<EventBGuard> added, EventBGuard g ->
					def newName = getUniqueName(g.getName(), guards + added)
					def newG = newName == g.getName() ? g : new EventBGuard(newName, g.getPredicate(), g.isTheorem(), g.getComment())
					added << newG
				}
				e = e.set(Guard.class, new ModelElementList<EventBGuard>(grds))
			}
		}
		newEM(e)
	}

	def EventModifier when(LinkedHashMap g) throws ModelGenerationException {
		guards(g)
	}

	def EventModifier when(String... conditions) throws ModelGenerationException {
		guards(validate('conditions',conditions))
	}

	def EventModifier where(LinkedHashMap g) throws ModelGenerationException {
		guards(g)
	}

	def EventModifier where(String... conditions) throws ModelGenerationException {
		guards(validate('conditions',conditions))
	}

	def EventModifier guards(LinkedHashMap guards) throws ModelGenerationException {
		EventModifier em = this
		guards.each { k,v ->
			em = em.guard(k,v)
		}
		em
	}

	def EventModifier guards(String... guards) throws ModelGenerationException {
		EventModifier em = this
		validate('guards', guards).each {
			em = em.guard(it)
		}
		em
	}

	def EventModifier theorem(LinkedHashMap theorem) throws ModelGenerationException {
		guard(theorem, true)
	}

	def EventModifier theorem(String theorem) throws ModelGenerationException {
		guard(validate('theorem',theorem), true)
	}

	def EventModifier guard(String pred, boolean theorem=false) throws ModelGenerationException {
		guard(theorem ? "thm0" : "grd0", pred, theorem)
	}

	def EventModifier guard(EventB predicate, boolean theorem=false) throws ModelGenerationException {
		guard(theorem ? "thm0" : "grd0", predicate, theorem)
	}

	def EventModifier guard(LinkedHashMap properties, boolean theorem=false) throws ModelGenerationException {
		Definition prop = getDefinition(properties)
		return guard(prop.label, prop.formula, theorem)
	}

	def EventModifier guard(String name, String pred, boolean theorem=false, String comment="") throws ModelGenerationException {
		guard(name, parsePredicate(pred), theorem, comment)
	}

	def EventModifier guard(String name, EventB pred, boolean theorem=false, String comment="") throws ModelGenerationException {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot at a guard to INITIALISATION")
		}
		def n = validate('name', name)
		def guard = new EventBGuard(getUniqueName(n, event.getAllGuards()), ensureType(pred, EvalElementType.PREDICATE), theorem, comment)
		newEM(event.addTo(Guard.class, guard))
	}

	def EventModifier removeGuard(String name) {
		def grd = event.guards.getElement(name)
		grd ? removeGuard(grd) : this
	}

	/**
	 * Remove a guard from an event
	 * @param guard to be removed
	 * @return whether or not the removal was successful
	 */
	def EventModifier removeGuard(EventBGuard guard) {
		newEM(event.removeFrom(Guard.class, guard))
	}

	def EventModifier then(LinkedHashMap assignments) throws ModelGenerationException {
		actions(assignments)
	}

	def EventModifier then(String... assignments) throws ModelGenerationException {
		actions(validate('assignments',assignments))
	}

	def EventModifier actions(LinkedHashMap actions) throws ModelGenerationException {
		EventModifier em = this
		actions.each { k,v ->
			em = em.action(k,v)
		}
		em
	}

	def EventModifier actions(String... actions) throws ModelGenerationException {
		EventModifier em = this
		validate('actions',actions).each {
			em = em.action(it)
		}
		em
	}

	def EventModifier action(LinkedHashMap properties) throws ModelGenerationException {
		Definition prop = getDefinition(properties)
		return action(prop.label, prop.formula)
	}

	def EventModifier action(String actionString) throws ModelGenerationException {
		action("act0", actionString)
	}

	def EventModifier action(EventB act) throws ModelGenerationException {
		action("act0", act)
	}

	def EventModifier action(String name, String act, String comment="") throws ModelGenerationException {
		action(name, parseFormula(act, EvalElementType.ASSIGNMENT), comment)
	}

	def EventModifier action(String name, EventB act, String comment="") throws ModelGenerationException {
		def n = validate('name', name)
		def a = new EventBAction(getUniqueName(n, event.getAllActions()), ensureType(act, EvalElementType.ASSIGNMENT), comment)
		action(a)
	}

	def EventModifier action(EventBAction act) {
		newEM(event.addTo(Action.class, act))
	}

	def EventModifier removeAction(String name) {
		def act = event.actions.getElement(name)
		act ? removeAction(act) : this
	}

	/**
	 * Remove an action from an event
	 * @param action to be removed
	 * @return whether or not the removal was successful
	 */
	def EventModifier removeAction(EventBAction action) {
		newEM(event.removeFrom(Action.class, action))
	}

	def EventModifier any(String... params) throws ModelGenerationException {
		parameters(validate('params', params))
	}

	def EventModifier parameters(String... parameters) throws ModelGenerationException {
		EventModifier em = this
		validate('parameters', parameters).each {
			em = em.parameter(it)
		}
		em
	}

	def EventModifier parameter(String parameter, String comment="") throws ModelGenerationException {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot add parameter to initialisation.")
		}
		parseIdentifier(parameter)
		def param = new EventParameter(parameter, comment)
		newEM(event.addTo(EventParameter.class, param))
	}

	def EventModifier removeParameter(String name) {
		def param = event.parameters.getElement(name)
		param ? removeParameter(param) : this
	}

	def EventModifier removeParameter(EventParameter parameter) {
		newEM(event.removeFrom(EventParameter.class, parameter))
	}

	def EventModifier with(String name, String predicate) throws ModelGenerationException {
		witness(validate('name',name), validate('predicate',predicate))
	}

	def EventModifier witness(LinkedHashMap properties, String comment="") throws ModelGenerationException {
		Map validated = validateProperties(properties, [for: String, with: String])
		witness(validated.for, validated.with,comment)
	}

		def EventModifier witness(String name, String predicate, String comment="") throws ModelGenerationException {
		parseIdentifier(name) // the label for a witness must be an abstract variable
		def w = new Witness(name, parsePredicate(predicate), comment)
		newEM(event.addTo(Witness.class, w))
	}

	def EventModifier removeWitness(String name) {
		def wit = event.witnesses.getElement(name)
		wit ? removeWitness(wit) : this
	}

	def EventModifier removeWitness(Witness w) {
		newEM(event.removeFrom(Witness.class, w))
	}

	def EventModifier setType(EventType type) {
		newEM(event.changeType(validate('type', type)))
	}

	def EventModifier addComment(String comment) {
		comment ? newEM(event.addTo(ElementComment.class, new ElementComment(comment))) : this
	}

	def EventModifier make(Closure definition) throws ModelGenerationException {
		runClosure definition
	}
}
