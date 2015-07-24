package de.prob.model.eventb

import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

/**
 * The {@link MachineModifier} provides an API to programmatically modify or
 * construct {@link EventBMachine}s. Basic elements can be added to the machine
 * via the methods beginning with 'add' (e.g. {@link #addInvariant(String)}).
 * <br>
 * Machines can also be constructed using JavaBuilder syntax which adds an element
 * and returns the {@link MachineModifier} object itself to allow the method calls
 * to be chained together.
 * <br>
 * For example: <br>
 * <code>modifier.var_block("x","x:NAT","x:=0").invariant("x < 10")</code>
 * <br>
 * These methods can then be put together to create a Groovy DSL.
 * <br>
 * <code>
 * modifier.make {<br>
 * <br>
 * var_block name: "x", invariant: "x:NAT", init: "x:=0"<br>
 * <br>
 * event(name: "inc") { action x:=x+1 }<br>
 * <br>
 * }<br>
 * </code>
 * @author Joy Clark
 */
class MachineModifier extends AbstractModifier {
	private final int invctr
	EventBMachine machine
	EventBModel model
	private eventModifiers = [:]

	def MachineModifier(EventBMachine machine) {
		this(machine, 0)
	}

	private MachineModifier(EventBMachine machine, int invctr) {
		this.machine = machine
		this.invctr = invctr
	}

	private newMM(EventBMachine machine) {
		new MachineModifier(machine, invctr)
	}

	def MachineModifier addSees(List<Context> seenContexts) {
		newMM(machine.set(Context.class, new ModelElementList<Context>(seenContexts)))
	}

	def MachineModifier addRefines(List<EventBMachine> refines) {
		newMM(machine.set(Machine.class, new ModelElementList<EventBMachine>(refines)))
	}

	def MachineModifier variables(String... variables) {
		MachineModifier mm = this
		variables.each {
			mm = mm.variable(it)
		}
		mm
	}

	/** adds a variable */
	def MachineModifier variable(String varName) {
		newMM(machine.addTo(Variable.class, new EventBVariable(it, null)))
	}

	def MachineModifier var_block(LinkedHashMap properties) {
		Map validated = validateProperties(properties, [name: String, invariant: Object, init: Object])
		var_block(validated.name, validated.invariant, validated.init)
	}

	def MachineModifier var_block(String name, String invariant, String init) {
		MachineModifier mm = variable(name)
		mm = mm.invariant(invariant)
		// TODO
		this
	}

	def MachineModifier var_block(String name, Map inv, Map init) {
		variable(name)
		invariant(inv)
		initialisation({ action init })
		this
	}

	def MachineModifier removeVariable(EventBVariable variable) {
		newMM(machine.removeFrom(Variable.class, variable))
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
		MachineModifier mm = this
		invariants.each { k,v ->
			mm = mm.invariant(k,v)
		}
		mm
	}

	def MachineModifier invariants(String... invariants) {
		MachineModifier mm = this
		invariants.each {
			mm = mm.invariant(it)
		}
		mm
	}

	def MachineModifier theorems(Map invariants) {
		MachineModifier mm = this
		invariants.each { k,v ->
			mm = mm.theorem(k,v)
		}
		mm
	}

	def MachineModifier theorems(String... invariants) {
		MachineModifier mm = this
		invariants.each {
			mm = mm.theorem(it)
		}
		mm
	}

	def MachineModifier theorem(LinkedHashMap properties) {
		invariant(properties, true)
	}

	def MachineModifier theorem(String thm) {
		invariant(thm, true)
	}

	def MachineModifier invariant(LinkedHashMap properties, boolean theorem=false) {
		Definition prop = getDefinition(properties)
		return invariant(prop.label, prop.formula, theorem)
	}

	def MachineModifier invariant(String pred, boolean theorem=false) {
		int ctr = invctr + 1
		new MachineModifier(invariant("i$ctr", pred, theorem).getMachine(),ctr)
	}

	def MachineModifier invariant(String name, String predicate, boolean theorem=false) {
		def newproofs = machine.getProofs().findAll { po ->
			!po.getName().endsWith("/INV")
		}

		def invariant = new EventBInvariant(name, predicate, theorem, Collections.emptySet())
		machine = machine.addTo(Invariant.class, invariant)
		machine = machine.set(ProofObligation.class, new ModelElementList<ProofObligation>(newproofs))
		newMM(machine)
	}

	/**
	 * Removes an invariant from the machine.
	 * @param invariant to be removed
	 * @return whether or not the removal was successful
	 */
	def MachineModifier removeInvariant(EventBInvariant invariant) {
		// only variant well-definedness may not use existing invariants in a prove
		// thus, these seem to be the only proof obligations we can keep
		def newproofs = machine.getProofs().findAll { po ->
			po.getName().endsWith("/VWD")
		}

		newMM(machine.removeFrom(Invariant.class, invariant)
				.set(ProofObligation.class, new ModelElementList<ProofObligation>(newproofs)))
	}

	def MachineModifier variant(String expression) {
		def variant = new Variant(expression, Collections.emptySet())
		newMM(machine.set(Variant.class, new ModelElementList([variant])))
	}

	def MachineModifier removeVariant(Variant variant) {
		newMM(machine.removeFrom(Variant.class, variant))
	}

	def EventModifier getInitialisation(boolean extended=false) {
		def refinedEvent = machine.refines.isEmpty() ? null : machine.refines[0].events.INITIALISATION
		getEvent("INITIALISATION", extended, refinedEvent)
	}

	def MachineModifier initialisation(LinkedHashMap properties) {
		if (properties["extended"] == true) {
			properties["name"] = "INITIALISATION"
			properties["refines"] = ["INITIALISATION"]
			event(properties, cls)
		}
		this
	}

	def MachineModifier initialisation(Closure cls) {
		event([name: "INITIALISATION"], cls)
	}

	def MachineModifier refine(LinkedHashMap properties, Closure cls={}) {
		properties["refines"] = [properties["name"]]
		event(properties, cls)
	}

	def MachineModifier event(LinkedHashMap properties, Closure cls={}) {
		validateProperties(properties, [name: String])

		def refinedEvents = properties["refines"]
		def refinedE  = []
		if (refinedEvents) {
			def props = validateProperties(properties, [refines: List])
			refinedEvents = props["refines"]
			assert refinedEvents.every { it instanceof String }
			refinedE = refinedEvents.each { n ->
				machine.getRefines().each { EventBMachine m ->
					def e = m.getEvent(n)
					if (e) {
						refinedE << e
					}
				}
			}
		}
		def type = properties["type"] ?: EventType.ORDINARY

		if (refinedEvents && refinedEvents.size() != refinedE.size()) {
			throw new IllegalArgumentException("Tried to refine events $refinedEvents, but only found $refinedE")
		}

		event(properties["name"], refinedE, type, properties["extended"], cls)
	}

	def MachineModifier event(String name, List<Event> refinedEvents, type, boolean extended, Closure cls={}) {
		def oldevent = machine.getEvent(name)
		def event = oldevent.changeType(type).toggleExtended(extended) ?: new Event(name, type, properties["extended"] == true)
		def em = new EventModifier(event, "INITIALISATION" == name).make(cls)
		def m = oldevent ? machine.replaceIn(Event.class, oldevent, em.getEvent()) : machine.addTo(Event.class, em.getEvent())
		return newMM(m)
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
	def MachineModifier removeEvent(Event event) {
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

	def MachineModifier make(Closure definition) {
		runClosure definition
	}


}
