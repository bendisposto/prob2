package de.prob.model.eventb

import de.prob.Main
import de.prob.model.representation.ElementComment
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.scripting.EventBFactory


public class ModelModifier extends AbstractModifier {

	EventBModel model

	/**
	 * Creates an interface to allow the user to mutate the model object.
	 * The user can also specify an additional parameter 'startProB' which will
	 * determine if a ProB instance will be bound to the new
	 * model class. If not, a ProB instance can be lazily created later by calling
	 * the getStateSpace() method on the model object.
	 * @param model to be copied
	 * @param startProB default = true
	 */
	def ModelModifier(EventBModel model) {
		this.model = model
	}

	def ModelModifier() {
		EventBFactory factory = Main.getInjector().getInstance(EventBFactory.class)
		this.model = factory.modelCreator.get()
	}

	def ModelModifier context(HashMap properties, Closure definition) {
		def props = validateProperties(properties, [name: String, "extends": [String,null]])
		def model = this.model
		def name = properties["name"]
		def oldcontext = model.getContexts().getElement(name)
		def c = oldcontext ?: new Context(name)

		def extended = c.getExtends() ? c.getExtends()[0] : null
		def ext = props["extends"]
		if (ext) {
			Context ctx = model.getContexts().getElement(ext)
			if (ctx == null) {
				throw new IllegalArgumentException("Tried to load context $ext but could not find it.")
			}
			if (extended) {
				model = model.removeRelationship(name, extended.getName(), ERefType.EXTENDS)
			}
			model.addRelationship(name, ctx.getName(), ERefType.EXTENDS)
			extended = ctx
		}

		def cm = new ContextModifier(c)
		if (extended) {
			cm = cm.setExtends(extended)
		}
		cm = cm.make(definition)
		model = oldcontext ? model.replaceIn(Context.class, oldcontext, cm.getContext()) :
				model.addTo(Context.class, cm.getContext())
		new ModelModifier(model)
	}

	def ModelModifier machine(HashMap properties, Closure definition) {
		def props = validateProperties(properties, [name: String, refines: [String,null], sees: [List,[]], comment: [String,null]])
		def model = this.model
		def name = props["name"]
		def oldmachine = model.getMachines().getElement(name)
		EventBMachine m = oldmachine ?: new EventBMachine(name)
		if (props["comment"]) {
			m = m.addTo(ElementComment.class, new ElementComment(props["comment"]))
		}

		EventBMachine refined = m.getRefines() ? m.getRefines()[0] : null
		def refines = props["refines"]
		if (refines) {
			EventBMachine machine = model.getMachines().getElement(refines)
			if (machine == null) {
				throw new IllegalArgumentException("Tried to load machine ${refines} but could not find it")
			}
			if (refined) {
				model = model.removeRelationship(name, refined.getName(), ERefType.REFINES)
			}
			model = model.addRelationship(name, machine.getName(), ERefType.REFINES)
			refined = machine
		}

		ModelElementList<Context> seenContexts = m.getSees()
		def sees = props["sees"].findAll { !seenContexts.hasProperty(it) }
		sees.each { c ->
			Context context = model.getContexts().getElement(c)
			if (context == null) {
				throw new IllegalArgumentException("Tried to load context $c but could not find it")
			}
			model = model.addRelationship(name, c, ERefType.SEES)
			seenContexts = seenContexts.addElement(context)
		}
		def mm = new MachineModifier(m)
		if (refined) {
			mm = mm.setRefines(refined)
		}
		mm = mm.setSees(seenContexts).make(definition)
		model = oldmachine ? model.replaceIn(Machine.class, oldmachine, mm.getMachine()) :
				model.addTo(Machine.class, mm.getMachine())
		new ModelModifier(model)
	}

	def ModelModifier refine(String machineName, String refinementName) {
		final EventBMachine m = model.getMachines().getElement(machineName)
		if (m == null) {
			throw new IllegalArgumentException("Can only refine an existing machine in the model")
		}

		def comment = m.getChildrenOfType(ElementComment.class) ? m.getChildrenOfType(ElementComment.class).collect { it.comment }.join("\n") : null
		ModelModifier modelM = machine(name: refinementName, refines: machineName, comment: comment) {
			m.variables.each {
				variable(it)
			}
			m.events.each { Event e ->
				refine(name: e.getName(), extended: true,
				comment: e.getChildrenOfType(ElementComment.class) ? e.getChildrenOfType(ElementComment.class).collect { it.comment }.join("\n") : null) {}
			}
		}
		modelM
	}

	def ModelModifier make(Closure definition) {
		runClosure definition
	}

	def ModelModifier replaceContext(EventBMachine oldContext, EventBMachine newContext) {
		new ModelModifier(model.replaceIn(Context.class, oldContext, newContext))
	}

	def ModelModifier replaceMachine(EventBMachine oldMachine, EventBMachine newMachine) {
		new ModelModifier(model.replaceIn(Machine.class, oldMachine, newMachine))
	}
}
