package de.prob.model.eventb

import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.Invariant
import de.prob.model.representation.ModelElementList

class MachineModifier {
	private UUID uuid = UUID.randomUUID()
	private ctr = 0
	EventBMachine machine

	def MachineModifier(EventBMachine machine) {
		this.machine = machine
	}

	/**
	 * Adds a variable to the given machine.
	 * @param variable to be added
	 * @param typingInvariant to specify the type of the variable
	 * @param initialisationAction to specify how the variable should be initialized
	 * @return the new {@link VariableBlock} that has been created containing the new elements
	 */
	def VariableBlock addVariable(String variable, String typingInvariant, String initialisationAction) {
		// proof obligations are invalidated by addInvariant
		// if we could check whether typingInvariant is in fact only typing,
		// we could remove just selected proof information
		def var = new EventBVariable(variable, null)
		def c = ctr++
		machine.variables << var
		def inv = addInvariant(typingInvariant)
		Event initialisation = machine.events.INITIALISATION
		def init = new EventBAction(initialisation, "generated-init-${uuid.toString()}-${c}", initialisationAction, Collections.emptySet())
		initialisation.actions << init
		def x = new VariableBlock(var, inv, init)
	}

	/**
	 * Removes a variable and its typing/initialisation information from the machine
	 * @param block containing the added variable, typing invariant, and initialisation
	 * @return if the removal of all elements from the machine was successful.
	 */
	def boolean removeVariableBlock(VariableBlock block) {
		// proof obligations are invalidated by removeInvariant
		// if we could check whether typingInvariant is in fact only typing,
		// we could remove just selected proof information
		def a = machine.variables.remove(block.getVariable())
		def b = removeInvariant(block.getTypingInvariant())
		def c = machine.events.INITIALISATION.actions.remove(block.getInitialisationAction())
		return a & b & c
	}

	/**
	 * Adds an invariant to a given machine
	 * @param predicate to be added as an invariant
	 * @return the {@link EventBInvariant} object that has been added to the machine
	 */
	def EventBInvariant addInvariant(String predicate) {
		// all proof information regarding invariant preservation might now be wrong - remove
		def iterator = machine.proofs.iterator()
		while(iterator.hasNext()) {
			if(iterator.next().name.endsWith("/INV")) {
				iterator.remove()
			}
		}
		
		def invariant = new EventBInvariant("generated-inv-{uuid.toString()}-${ctr++}", predicate, false, Collections.emptySet())
		machine.invariants << invariant
		machine.getChildrenOfType(Invariant.class) << invariant
		invariant
	}

	/**
	 * Removes an invariant from the machine.
	 * @param invariant to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeInvariant(EventBInvariant invariant) {
		// only variant well-definedness may not use existing invariants in a prove
		// thus, these seem to be the only proof obligations we can keep
		def iterator = machine.proofs.iterator()
		while(iterator.hasNext()) {
			if(!iterator.next().name.endsWith("/VWD")) {
				iterator.remove()
			}
		}
		
		def a = machine.getChildrenOfType(Invariant.class).remove(invariant)
		def b = machine.invariants.remove(invariant)
		return a && b
	}


	/**
	 * This method searches for the {@link Event} with the specified name in the
	 * {@link EventBMachine}. If found, an {@link EventModifier} is created to allow the
	 * modification of the specified event. Otherwise, an {@link Event} is added to the
	 * machine via {@link #addEvent(String)}
	 * @param name of event to be added
	 * @return an {@link EventModifier} to modify the specified {@link Event}
	 */
	def EventModifier getEvent(String name) {
		if (machine.events.hasProperty(name)) {
			return new EventModifier(machine.events.getProperty(name))
		}
		addEvent(name)
	}

	/**
	 * Creates a new {@link Event} object and adds it to the machine.
	 * An {@link EventModifier} object is then created and returned to allow
	 * the modification of the specified {@link Event}.
	 * NOTE: This will override an existing {@link Event} in the model with
	 * the same name. To modify an existing {@link Event} use {@link #getEvent(String)}
	 * @param name of event to be added
	 * @return an {@link EventModifier} to modify the specified {@link Event}
	 */
	def EventModifier addEvent(String name) {
		removePOsForEvent(name)
		Event event = new Event(machine, name, EventType.ORDINARY)
		event.addActions(new ModelElementList<EventBAction>())
		event.addGuards(new ModelElementList<EventBGuard>())
		event.addParameters(new ModelElementList<EventParameter>())
		event.addRefines(new ModelElementList<Event>())
		event.addWitness(new ModelElementList<Witness>())
		machine.events << event
		return new EventModifier(event)
	}

	/**
	 * Generates a new {@link Event} in the machine that is identical to
	 * the specified event for copying. The new {@link Event} object will
	 * have the specified name. If an existing {@link Event} in the machine
	 * has the same name, this will be overwritten.
	 * @param event to be duplicated
	 * @param newName of the cloned event
	 * @return {@link EventModifier} object of the duplicated event to allow
	 * for further modification
	 */
	def EventModifier duplicateEvent(Event event, String newName) {
		removePOsForEvent(newName)
		Event event2 = ModelModifier.cloneEvent(machine, event, newName)
		machine.events << event2
		return new EventModifier(event2)
	}

	/**
	 * Removes an event from the machine.
	 * @param event to be removed
	 * @return whether or not the removal was successful
	 */
	def boolean removeEvent(Event event) {
		removePOsForEvent(event.name)
		return machine.events.remove(event)
	}
	
	def removePOsForEvent(String name) {
		def iterator = machine.proofs.iterator()
		while(iterator.hasNext()) {
			if(iterator.next().name.startWith(name)) {
				iterator.remove()
			}
		}
	}

	def List<EventModifier> getEvents() {
		return machine.events.collect { new EventModifier(it) }
	}
}
