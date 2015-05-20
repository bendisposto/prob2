package de.prob.model.eventb




class EventModifier extends AbstractModifier {
	private UUID uuid = UUID.randomUUID()
	private ctr = 0
	def Event event
	boolean initialisation

	def EventModifier(Event event, initialisation=false) {
		this.event = event
		this.initialisation = initialisation
	}
	
	def EventModifier guards(Map guards) {
		guards.each { k,v ->
			guard(k,v)
		}
		this
	}
	
	def EventModifier theorem(LinkedHashMap properties) {
		guard(properties, true)
	}

	def EventModifier guard(LinkedHashMap properties, boolean theorem=false) {
		Definition prop = getDefinition(properties)
		return guard(prop.label, prop.formula, theorem)
	}

	def EventModifier guard(String name, String pred, boolean theorem=false) {
		if (initialisation) {
			throw new IllegalArgumentException("Cannot at a guard to INITIALISATION")
		}
		def guard = new EventBGuard(event, name, pred, theorem, Collections.emptySet())
		event.guards << guard
		this
	}

	/**
	 * Add a guard to an event
	 * @param predicate to be added as guard
	 * @return {@link EventBGuard} that has been added to the event
	 */
	def EventBGuard addGuard(String predicate) {
		def guard = new EventBGuard(event, "gen-guard-${uuid.toString()}-${ctr++}", predicate, false, Collections.emptySet())
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
	
	def EventModifier actions(Map actions) {
		actions.each { k,v ->
			action(k,v)
		}
		this
	}

	def EventModifier action(LinkedHashMap properties) {
		Definition prop = getDefinition(properties)
		return action(prop.label, prop.formula)
	}

	def EventModifier action(String name, String action) {
		def act = new EventBAction(event, name, action, Collections.emptySet())
		event.actions << act
		this
	}

	/**
	 * Add an action to an event
	 * @param action to be added to the event
	 * @return the {@link EventBAction} that has been added to the {@link Event}
	 */
	def EventBAction addAction(String action) {
		def a = new EventBAction(event, "gen-action-${uuid.toString()}-${ctr++}", action, Collections.emptySet())
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
		def param = new EventParameter(event, parameter)
		event.parameters << param
		def guard = new EventBGuard(event, "gen-typing-guard-${uuid.toString()}-${ctr++}", typingGuard, false, Collections.emptySet())
		event.guards << guard
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
	
	def EventModifier witness(Map definition) {
		Definition d = definition as Definition
		witness(d.label, d.formula)
	}
	
	def EventModifier witness(String name, String code) {
		event.witnesses << new Witness(event, name, code, Collections.emptySet())
		this
	}

	def EventModifier make(Closure definition) {
		runClosure definition
		this
	}
}
