package de.prob.model.eventb

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.Block
import de.prob.model.representation.BEvent
import de.prob.model.representation.ElementComment
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
public class MachineModifier extends AbstractModifier {
	EventBMachine machine
	EventBModel model

	public MachineModifier(EventBMachine machine, Set<IFormulaExtension> typeEnvironment = Collections.emptySet()) {
		super(typeEnvironment)
		this.machine = validate('machine',machine)
	}

	private newMM(EventBMachine machine) {
		new MachineModifier(machine, typeEnvironment)
	}

	def MachineModifier setSees(ModelElementList<Context> seenContexts) {
		newMM(machine.set(Context.class, validate("seenContexts", seenContexts)))
	}

	def MachineModifier setRefines(EventBMachine refined) {
		validate("refined", refined)
		newMM(machine.set(Machine.class, new ModelElementList<EventBMachine>([refined])))
	}

	def MachineModifier variables(String... variables) {
		MachineModifier mm = this
		validate("variables", variables).each {
			mm = mm.variable(it)
		}
		mm
	}

	def MachineModifier variable(String varName, String comment="") {
		parseIdentifier(varName)
		variable(new EventBVariable(varName, null, comment))
	}

	def MachineModifier variable(EventBVariable variable) {
		newMM(machine.addTo(Variable.class, variable))
	}

	def MachineModifier var_block(LinkedHashMap properties) {
		Map validated = validateProperties(validate("properties", properties), [name: String, invariant: Object, init: Object])
		var_block(validated.name, validated.invariant, validated.init)
	}

	def MachineModifier var_block(String name, String invariant, String init) {
		MachineModifier mm = variable(name)
		mm = mm.invariant(invariant)
		mm = mm.initialisation({ action init })
		mm
	}

	def MachineModifier var_block(String name, Map inv, Map init) {
		MachineModifier mm = variable(name)
		mm = mm.invariant(inv)
		mm = mm.initialisation({ action init })
		mm
	}

	def MachineModifier removeVariable(String name) {
		def var = machine.variables.getElement(name)
		var ? removeVariable(var) : this
	}

	def MachineModifier removeVariable(EventBVariable variable) {
		newMM(machine.removeFrom(Variable.class, variable))
	}

	def MachineModifier invariants(Map invariants) {
		MachineModifier mm = this
		validate("invariants", invariants).each { k,v ->
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
		invariant(validate("thm", thm), true)
	}

	def MachineModifier theorem(String name, String pred) {
		invariant(validate("name", name), validate("pred", pred), true)
	}

	def MachineModifier invariant(LinkedHashMap properties, boolean theorem=false) {
		Definition prop = getDefinition(properties)
		return invariant(prop.label, prop.formula, theorem)
	}

	def MachineModifier invariant(String pred, boolean theorem=false) {
		int ctr = extractCounter("inv", machine.invariants) + 1
		invariant("inv$ctr", validate("pred", pred), theorem)
	}

	def MachineModifier invariant(String name, String predicate, boolean theorem=false, String comment="") {
		validateAll(name, predicate)

		def newproofs = machine.getProofs().findAll { ProofObligation po ->
			!po.getName().endsWith("/INV")
		}

		def invariant = new EventBInvariant(name, parsePredicate(predicate), theorem, comment)
		machine = machine.addTo(Invariant.class, invariant)
		machine = machine.set(ProofObligation.class, new ModelElementList<ProofObligation>(newproofs))
		newMM(machine)
	}

	def MachineModifier removeInvariant(String name) {
		def inv = machine.invariants.getElement(name)
		inv ? removeInvariant(inv) : this
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
			po.getName().equals("VWD")
		}

		newMM(machine.removeFrom(Invariant.class, invariant)
				.set(ProofObligation.class, new ModelElementList<ProofObligation>(newproofs)))
	}

	def MachineModifier variant(String expression, String comment="") {
		variant(new Variant(parseExpression(expression), comment))
	}

	def MachineModifier variant(Variant variant) {
		def mm = removePOsForVariant()
		newMM(mm.getMachine().set(Variant.class, new ModelElementList([variant])))
	}

	def MachineModifier removePOsForVariant() {
		def newproofs = machine.getProofs().findAll { po ->
			!(po.getName().equals("VWD") ||
					po.getName().equals("FIN") ||
					po.getName().endsWith("/VAR") ||
					po.getName().endsWith("/NAT"))
		}

		newMM(machine.set(ProofObligation.class, new ModelElementList<ProofObligation>(newproofs)))
	}

	def MachineModifier removeVariant(Variant variant) {
		if (!machine.getChildrenOfType(Variant.class).contains(variant)) {
			return this
		}
		def mm = removePOsForVariant()
		newMM(mm.getMachine().removeFrom(Variant.class, variant))
	}

	def MachineModifier initialisation(LinkedHashMap properties) {
		if (properties["extended"] == true) {
			return initialisation({},true)
		}
		this
	}

	def MachineModifier initialisation(Closure cls, boolean extended=false) {
		def refines = machine.getRefines().isEmpty() ? null : "INITIALISATION"
		event("INITIALISATION", refines, EventType.ORDINARY, extended, null, validate("cls",cls))
	}

	def MachineModifier refine(LinkedHashMap properties, Closure cls={}) {
		validate("properties", properties)
		properties["refines"] = properties["name"]
		event(properties, validate("cls", cls))
	}

	def MachineModifier event(LinkedHashMap properties, Closure cls={}) {
		def props = validateProperties(properties, [name: String, refines: [String, null],
			extended: [Boolean, false], comment: [String, null], type: [
				EventType,
				EventType.ORDINARY
			]])

		event(props["name"], props["refines"], props["type"],props["extended"],props["comment"], validate("cls",cls))
	}

	def MachineModifier event(String name, String refinedEvent, EventType type, boolean extended, String comment=null,Closure cls={} ) {
		validateAll(name, type, cls)
		def mm = removePOsForEvent(name)
		def oldevent = machine.getEvent(name)
		def event = oldevent ? oldevent.changeType(type).toggleExtended(extended) : new Event(name, type, extended)
		if (comment) {
			event = event.addTo(ElementComment.class, new ElementComment(comment))
		}
		def em = new EventModifier(event, "INITIALISATION" == name, typeEnvironment).make(cls)
		em = refinedEvent ? em.refines(refinedEvent) : em
		def m = mm.getMachine()
		if (oldevent) {
			m = m.replaceIn(BEvent.class, oldevent, em.getEvent())
		} else {
			m = m.addTo(BEvent.class, em.getEvent())
		}
		newMM(m)
	}

	/**
	 * Generates a new {@link Event} in the machine that is identical to
	 * the specified event for copying. The new {@link Event} object will
	 * have the specified name. If an existing {@link Event} in the machine
	 * has the same name, this will be overwritten.
	 * @param name of the event to be duplicated
	 * @param newName of the cloned event
	 */
	def MachineModifier duplicateEvent(String eventName, String newName) {
		validateAll(eventName, newName)
		MachineModifier mm = removePOsForEvent(newName)
		Event event = machine.getEvent(eventName)
		if (!event) {
			throw new IllegalArgumentException("Can only duplicate an event that exists! Event with name $eventName was not found.")
		}
		Event event2 = new Event(newName, event.type, event.extended, event.children)
		def oldE = mm.getMachine().events.getElement(newName)
		def m = oldE ? mm.getMachine().replaceIn(BEvent.class, oldE, event2) : mm.getMachine().addTo(BEvent.class, event2)
		return newMM(m)
	}

	def MachineModifier removeEvent(String name) {
		def evt = machine.events.getElement(name)
		evt ? removeEvent(evt) : this
	}

	/**
	 * Removes an event from the machine.
	 * @param event to be removed
	 * @return whether or not the removal was successful
	 */
	def MachineModifier removeEvent(Event event) {
		MachineModifier mm = removePOsForEvent(event.name)
		newMM(mm.getMachine().removeFrom(BEvent.class, event))
	}

	def MachineModifier removePOsForEvent(String name) {
		def proofs = machine.getProofs()
		proofs.each {
			if (it.name.startsWith(validate('name', name) + "/")) {
				proofs = proofs.removeElement(it)
			}
		}
		newMM(machine.set(ProofObligation.class, proofs))
	}

	def MachineModifier addComment(String comment) {
		comment ? newMM(machine.addTo(ElementComment.class, new ElementComment(comment))) : this
	}

	def MachineModifier algorithm(Closure definition) {
		newMM(machine.addTo(Block.class, new Block().make(validate('definition',definition))))
	}

	def MachineModifier make(Closure definition) {
		runClosure definition
	}



}
