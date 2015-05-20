package de.prob.model.eventb

import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.ModelElementList

class MachineModifier extends AbstractModifier {
	private UUID uuid = UUID.randomUUID()
	private ctr = 0
	EventBMachine machine
	EventBModel model
	private eventModifiers = [:]

	def MachineModifier(EventBMachine machine, List<Context> seenContexts, List<EventBMachine> refined) {
		this.machine = machine
		this.machine.addSees(new ModelElementList<Context>(seenContexts))
		this.machine.addRefines(new ModelElementList<EventBMachine>(refined))
	}

	def MachineModifier variables(String... variables) {
		variables.each { variable it }
		this
	}
	
	/** adds a variable */
	def MachineModifier variable(String varName) {
		machine.variables << new EventBVariable(varName, null)
		this
	}
	
	def MachineModifier var_block(LinkedHashMap properties) {
		Map validated = validateProperties(properties, [name: String, invariant: Definition, init: Definition])
		variable(validated["name"])
		invariant(validated["invariant"].label, validated["invariant"].formula)
		initialisation({
			action validated["init"].label, validated["init"].formula
		})
		this
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
	
	def MachineModifier invariants(Map invariants) {
		invariants.each { k,v ->
			invariant(k,v)
		}
		this
	}
	
	def MachineModifier theorem(LinkedHashMap properties) {
		invariant(properties, true)
	}

	def MachineModifier invariant(LinkedHashMap properties, boolean theorem=false) {
		Definition prop = getDefinition(properties)
		return invariant(prop.label, prop.formula, theorem)
	}

	def MachineModifier invariant(String name, String pred, boolean theorem=false) {
		def inv = new EventBInvariant(name, pred, theorem, Collections.emptySet())
		machine.invariants << inv
		machine.allInvariants << inv
		this
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
		machine.allInvariants << invariant
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

		def a = machine.allInvariants.remove(invariant)
		def b = machine.invariants.remove(invariant)
		return a && b
	}

	def MachineModifier initialisation(LinkedHashMap properties) {
		if (properties["extended"] == true) {
			getEvent("INITIALISATION", true)
		}
		this
	}

	def MachineModifier initialisation(Closure cls) {
		def refinedEvent = machine.refines.isEmpty() ? null : machine.refines[0].events.INITIALISATION
		getEvent("INITIALISATION", false, refinedEvent).make(cls)
		this
	}
	
	def MachineModifier refine(LinkedHashMap properties, Closure cls={}) {
		properties["refines"] = properties["name"]
		event(properties, cls)
	}

	def MachineModifier event(LinkedHashMap properties, Closure cls={}) {
		validateProperties(properties, [name: String])
		def refinedEvent = properties["refines"]
		def event
		if (refinedEvent != null) {
			machine.refines.each {
				def e = it.events.find { it.getName() == refinedEvent}
				if (e != null ) {
					event = e
				}
			}
		}
		if (refinedEvent != null && event == null) {
			throw new IllegalArgumentException("Tried to refine event $refinedEvent with $eventName, but could not find event in the refined machine ")
		}

		getEvent(properties["name"], properties["extended"] == true, event).make(cls)
		this
	}


	/**
	 * This method searches for the {@link Event} with the specified name in the
	 * {@link EventBMachine}. If found, an {@link EventModifier} is created to allow the
	 * modification of the specified event. Otherwise, an {@link Event} is added to the
	 * machine via {@link #addEvent(String)}
	 * @param name of event to be added
	 * @return an {@link EventModifier} to modify the specified {@link Event}
	 */
	def EventModifier getEvent(String name, boolean extended= false, Event refinedEvent= null) {
		if (eventModifiers[name]) {
			return eventModifiers[name]
		}
		if (machine.events.hasProperty(name)) {
			def x = new EventModifier(machine.events.getProperty(name), name == "INITIALISATION")
			eventModifiers[name] = x
			return x
		}
		eventModifiers[name] = addEvent(name, extended, refinedEvent)
		eventModifiers[name]
	}

	/**
	 * Creates a new {@link Event} object and adds it to the machine.
	 * An {@link EventModifier} object is then created and returned to allow
	 * the modification of the specified {@link Event}.
	 * @param name of event to be added
	 * @return an {@link EventModifier} to modify the specified {@link Event}
	 */
	def EventModifier addEvent(String name, boolean extended=false, Event refinedEvent=null) {
		removePOsForEvent(name)
		Event event = new Event(machine, name, EventType.ORDINARY, false)
		event.addActions(new ModelElementList<EventBAction>())
		event.addGuards(new ModelElementList<EventBGuard>())
		event.addParameters(new ModelElementList<EventParameter>())
		def refines = refinedEvent ? [refinedEvent]: []
		event.addRefines(new ModelElementList<Event>(refines))
		event.addWitness(new ModelElementList<Witness>())
		machine.events << event
		new EventModifier(event, name == "INITIALISATION")
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
		def modifier = new EventModifier(event2)
		eventModifiers[newName] = modifier
		return modifier
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
			if(iterator.next().name.startsWith(name)) {
				iterator.remove()
			}
		}
	}

	def List<EventModifier> getEvents() {
		return machine.events.collect { new EventModifier(it) }
	}

	def MachineModifier make(Closure definition) {
		runClosure definition
		this
	}


}
