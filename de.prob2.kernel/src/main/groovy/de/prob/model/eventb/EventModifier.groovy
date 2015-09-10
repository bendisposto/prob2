package de.prob.model.eventb

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.Action
import de.prob.model.representation.ElementComment
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class EventModifier extends AbstractModifier {
	private final actctr
	private final grdctr
	def Event event
	boolean initialisation

	public EventModifier(Event event, boolean initialisation=false, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.initialisation = initialisation
		this.actctr = extractCounter("act",event.actions)
		this.event = event
		this.grdctr = extractCounter("grd",event.guards)
	}

	private EventModifier newEM(Event event) {
		return new EventModifier(event, initialisation, typeEnvironment)
	}

	def EventModifier refines(String name) {
		return newEM(event.set(Event.class, new ModelElementList<Event>([
			new Event(validate('name',name), EventType.ORDINARY, false)
		])))
	}

	def EventModifier when(LinkedHashMap g) {
		guards(g)
	}

	def EventModifier when(String... conditions) {
		guards(validate('conditions',conditions))
	}

	def EventModifier where(LinkedHashMap g) {
		guards(g)
	}

	def EventModifier where(String... conditions) {
		guards(validate('conditions',conditions))
	}

	def EventModifier guards(LinkedHashMap guards) {
		EventModifier em = this
		guards.each { k,v ->
			em = em.guard(k,v)
		}
		em
	}

	def EventModifier guards(String... guards) {
		EventModifier em = this
		validate('guards', guards).each {
			em = em.guard(it)
		}
		em
	}

	def EventModifier theorem(LinkedHashMap theorem) {
		guard(theorem, true)
	}

	def EventModifier theorem(String theorem) {
		guard(validate('theorem',theorem), true)
	}

	def EventModifier guard(String pred, boolean theorem=false) {
		def ctr = grdctr + 1
		guard("grd$ctr", pred, theorem)
	}

	def EventModifier guard(LinkedHashMap properties, boolean theorem=false) {
		Definition prop = getDefinition(properties)
		return guard(prop.label, prop.formula, theorem)
	}

	def EventModifier guard(String name, String pred, boolean theorem=false, String comment="") {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot at a guard to INITIALISATION")
		}
		def guard = new EventBGuard(validate('name',name), parsePredicate(pred), theorem, comment)
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

	def EventModifier then(LinkedHashMap assignments) {
		actions(assignments)
	}

	def EventModifier then(String... assignments) {
		actions(validate('assignments',assignments))
	}

	def EventModifier actions(LinkedHashMap actions) {
		EventModifier em = this
		actions.each { k,v ->
			em = em.action(k,v)
		}
		em
	}

	def EventModifier actions(String... actions) {
		EventModifier em = this
		validate('actions',actions).each {
			em = em.action(it)
		}
		em
	}

	def EventModifier action(LinkedHashMap properties) {
		Definition prop = getDefinition(properties)
		return action(prop.label, prop.formula)
	}

	def EventModifier action(String actionString) {
		int ctr = actctr + 1
		action("act$ctr", actionString)
	}

	def EventModifier action(String name, String action, String comment="") {
		def a = new EventBAction(validate('name',name), parseFormula(action, EvalElementType.ASSIGNMENT), comment)
		newEM(event.addTo(Action.class, a))
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

	def EventModifier any(String... params) {
		parameters(validate('params', params))
	}

	def EventModifier parameters(String... parameters) {
		EventModifier em = this
		validate('parameters', parameters).each {
			em = em.parameter(it)
		}
		em
	}

	def EventModifier parameter(String parameter, String comment="") {
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

	def EventModifier with(String name, String predicate) {
		witness(validate('name',name), validate('predicate',predicate))
	}

	def EventModifier witness(LinkedHashMap properties, String comment="") {
		Map validated = validateProperties(properties, [for: String, with: String])
		witness(validated.for, validated.with,comment)
	}

	def EventModifier witness(String name, String predicate, String comment="") {
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

	def EventModifier make(Closure definition) {
		runClosure definition
	}
}
