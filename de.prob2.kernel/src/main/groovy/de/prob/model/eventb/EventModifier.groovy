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
	private final actctr
	private final grdctr
	def Event event
	boolean initialisation

	public EventModifier(Event event, boolean initialisation=false, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.initialisation = initialisation
		def actions = []
		event.getRefines().inject(actions) { List<EventBAction> acc, Event e -> acc.addAll(e.actions); acc}
		actions.addAll(event.actions)
		def guards = []
		event.getRefines().inject(guards) { List<EventBGuard> acc, Event e -> acc.addAll(e.guards); acc }
		guards.addAll(event.guards)
		this.actctr = extractCounter("act", actions)
		this.event = event
		this.grdctr = extractCounter("grd", guards)
	}

	private EventModifier newEM(Event event) {
		return new EventModifier(event, initialisation, typeEnvironment)
	}

	def EventModifier refines(Event refinedEvent, boolean extended) {
		validate('refinedEvent', refinedEvent)
		Event e = event.toggleExtended(extended)
		e = e.set(Event.class, new ModelElementList<Event>([refinedEvent]))
		if (extended) {
			def actctr = extractCounter("act", refinedEvent.actions)
			if (actctr > -1) {
				e = e.set(Action.class, new ModelElementList<EventBAction>(e.actions.collect { EventBAction a ->
					a.getName().startsWith("act") ? new EventBAction("act${++actctr}", a.getCode(), a.getComment()) : a
				}))
			}
			def grdctr = extractCounter("grd", refinedEvent.guards)
			if (grdctr > -1) {
				e = e.set(Guard.class, new ModelElementList<EventBGuard>(e.guards.collect { EventBGuard g ->
					g.getName().startsWith("grd") ? new EventBGuard("grd${++grdctr}", g.getPredicate(), g.isTheorem(), g.getComment()) : g
				}))
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
		def ctr = grdctr + 1
		guard("grd$ctr", pred, theorem)
	}

	def EventModifier guard(EventB predicate, boolean theorem=false) throws ModelGenerationException {
		def ctr = grdctr + 1
		guard("grd$ctr", predicate, theorem)
	}

	def EventModifier guard(LinkedHashMap properties, boolean theorem=false) throws ModelGenerationException {
		Definition prop = getDefinition(properties)
		return guard(prop.label, prop.formula, theorem)
	}

	def EventModifier guard(String name, String pred, boolean theorem=false, String comment="") throws ModelGenerationException {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot at a guard to INITIALISATION")
		}
		def guard = new EventBGuard(validate('name',name), parsePredicate(pred), theorem, comment)
		newEM(event.addTo(Guard.class, guard))
	}

	def EventModifier guard(String name, EventB pred, boolean theorem=false, String comment="") throws ModelGenerationException {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot at a guard to INITIALISATION")
		}
		def guard = new EventBGuard(validate('name',name), ensureType(pred, EvalElementType.PREDICATE), theorem, comment)
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
		int ctr = actctr + 1
		action("act$ctr", actionString)
	}

	def EventModifier action(EventB act) throws ModelGenerationException {
		int ctr = actctr + 1
		action("act$ctr", act)
	}

	def EventModifier action(String name, String action, String comment="") throws ModelGenerationException {
		def a = new EventBAction(validate('name',name), parseFormula(action, EvalElementType.ASSIGNMENT), comment)
		newEM(event.addTo(Action.class, a))
	}

	def EventModifier action(String name, EventB action, String comment="") throws ModelGenerationException {
		def a = new EventBAction(validate('name',name), ensureType(action, EvalElementType.ASSIGNMENT), comment)
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
