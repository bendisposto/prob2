package de.prob.model.eventb

import de.prob.model.eventb.Event.EventType




class EventModifier extends AbstractModifier {
	private actctr = 0
	private grdctr = 0
	def Event event
	boolean initialisation

	def EventModifier(Event event, initialisation=false) {
		this.event = event
		this.initialisation = initialisation
	}
	
	private String genActLabel() {
		return "ac" + actctr++
	}
	
	private String genGrdLabel() {
		return "g" + grdctr++
	}
	
	def EventModifier when(Map g) {
		guards(g)
	}
	
	def EventModifier when(String... g) {
		guards(g)
	}
	
	def EventModifier where(Map g) { 
		guards(g)
	}
	
	def EventModifier where(String... g) {
		guards(g)
	}
	
	def EventModifier guards(Map guards) {
		guards.each { k,v ->
			guard(k,v)
		}
		this
	}
	
	def EventModifier guards(String... grds) {
		grds.each {
			guard(it)
		}
		this
	}
	
	def EventModifier theorem(LinkedHashMap properties) {
		guard(properties, true)
	}
	
	def EventModifier theorem(String pred) {
		guard(genGrdLabel(), pred, true)		
	}

	def EventModifier guard(LinkedHashMap properties, boolean theorem=false) {
		Definition prop = getDefinition(properties)
		return guard(prop.label, prop.formula, theorem)
	}
	
	def EventModifier guard(String pred, boolean theorem=false) {
		guard(genGrdLabel(), pred, theorem)
	}

	def EventModifier guard(String name, String pred, boolean theorem=false) {
		addGuard(name, pred, theorem)
		this
	}

	/**
	 * Add a guard to an event
	 * @param predicate to be added as guard
	 * @return {@link EventBGuard} that has been added to the event
	 */
	def EventBGuard addGuard(String predicate, boolean theorem=false) {
		addGuard(genGrdLabel(), predicate, theorem)
	}
	
	def EventBGuard addGuard(String name, String pred, boolean theorem=false) {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot at a guard to INITIALISATION")
		}
		def guard = new EventBGuard(event, name, pred, theorem, Collections.emptySet())
		event.guards << guard
		guard
	}

	/**
	 * Remove a guard from an event
	 * @param guard to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeGuard(EventBGuard guard) {
		return event.guards.remove(guard)
	}
	
	def EventModifier then(Map acts) {
		actions(acts)
	}
	
	def EventModifier then(String... acts) {
		actions(acts)
	}
	
	def EventModifier actions(Map actions) {
		actions.each { k,v ->
			action(k,v)
		}
		this
	}
	
	def EventModifier actions(String... actions) {
		actions.each {
			action(it)
		}
		this
	}

	def EventModifier action(LinkedHashMap properties) {
		Definition prop = getDefinition(properties)
		return action(prop.label, prop.formula)
	}

	def EventModifier action(String actionString) {
		action(genActLabel(), actionString)
		this
	}
	
	def EventModifier action(String name, String action) {
		addAction(name, action)
		this
	}
	


	/**
	 * Add an action to an event
	 * @param action to be added to the event
	 * @return the {@link EventBAction} that has been added to the {@link Event}
	 */
	def EventBAction addAction(String action) {
		addAction(genActLabel(), action)
	}
	
	def EventBAction addAction(String name, String action) {
		def a = new EventBAction(event, name, action, Collections.emptySet())
		event.actions << a
		a
	}

	/**
	 * Remove an action from an event
	 * @param action to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeAction(EventBAction action) {
		return event.actions.remove(action)
	}
	
	def EventModifier parameters(String... parameters) {
		parameters.each {
			parameter(it)
		}
		this
	}

	def EventModifier parameter(String parameter) {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot add parameter to initialisation.")
		}
		def param = new EventParameter(event, parameter)
		event.parameters << param
		this
	}

	/**
	 * Add a parameter to an event
	 * @param parameter to be added to the event
	 * @return the {@link ParameterBlock} containing the elements added to the event
	 */
	def ParameterBlock addParameter(String parameter, String typingGuard) {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot add parameter to initialisation.")
		}
		def param = new EventParameter(event, parameter)
		event.parameters << param
		def guard = addGuard(typingGuard)
		new ParameterBlock(param, guard)
	}

	/**
	 * Remove a parameter and its typing guard (contained in the {@link ParameterBlock})
	 * from the event
	 * @param block containing the elements to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeParameter(ParameterBlock block) {
		def a = event.parameters.remove(block.parameter)
		def b = event.guards.remove(block.typingGuard)
		return a & b
	}
	
	def EventModifier witness(Map properties) {
		Map validated = validateProperties(properties, [for: String, with: String])
		witness(validated.for, validated.with)
	}
	
	def EventModifier witness(String name, String code) {
		event.witnesses << new Witness(event, name, code, Collections.emptySet())
		this
	}
	
	def EventModifier setType(EventType type) {
		event.type = type
		this
	}

	def EventModifier make(Closure definition) {
		runClosure definition
		this
	}
}
