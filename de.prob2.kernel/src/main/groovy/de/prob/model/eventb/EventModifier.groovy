package de.prob.model.eventb


class EventModifier {
	private UUID uuid = UUID.randomUUID()
	private ctr = 0
	def Event event

	def EventModifier(Event event) {
		this.event = event
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
}
