package de.prob.model.eventb

import de.prob.Main
import de.prob.model.representation.Machine
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
		validateProperties(properties, [name: String])
		def model = this.model
		def name = properties["name"]
		def oldcontext = model.getContexts().getElement(name)
		def c = oldcontext ?: new Context(name)

		def ext = properties["extends"] ?: []
		def extended = ext.collect { co ->
			Context ctx = model.getContexts().getElement(co)
			if (ctx == null) {
				throw new IllegalArgumentException("Tried to load context $co but could not find it.")
			}
			model.addRelationship(name, co, ERefType.EXTENDS)
			ctx
		}
		def cm = new ContextModifier(c).addExtends(extended).make(definition)
		model = oldcontext ? model.replaceIn(Context.class, oldcontext, cm.getContext()) :
				model.addTo(Context.class, cm.getContext())
		new ModelModifier(model)
	}

	def ModelModifier machine(HashMap properties, Closure definition) {
		validateProperties(properties, [name: String])
		def model = this.model
		def name = properties["name"]
		def oldmachine = model.getMachines().getElement(name)
		def m = oldmachine ?: new EventBMachine(name)

		def refines = properties["refines"] ?: []
		def refined = refines.collect { ma ->
			EventBMachine machine = model.getMachines().getElement(ma)
			if (machine == null) {
				throw new IllegalArgumentException("Tried to load machine $ma but could not find it")
			}
			model = model.addRelationship(name, ma, ERefType.REFINES)
			machine
		}

		def sees = properties["sees"] ?: []
		def seenContexts = sees.collect { c ->
			Context context = model.getContexts().getElement(c)
			if (context == null) {
				throw new IllegalArgumentException("Tried to load context $c but could not find it")
			}
			model = model.addRelationship(name, c, ERefType.SEES)
			context
		}
		def mm = new MachineModifier(m).addSees(seenContexts).addRefines(refined).make(definition)
		model = oldmachine ? model.replaceIn(Machine.class, oldmachine, mm.getMachine()) :
				model.addTo(Machine.class, mm.getMachine())
		new ModelModifier(model)
	}

	def ModelModifier make(Closure definition) {
		runClosure definition
	}

	//	/**
	//	 * Finds the machine with the specified name and returns a {@link MachineModifier} object
	//	 * to allow the modification of the machine elements.
	//	 * @param machineName of the machine to be modified
	//	 * @return a {@link MachineModifier} object to allow the modification of machine with name
	//	 * 	machineName or <code>null</code>, if the specified machine does not exist
	//	 */
	//	def MachineModifier getMachine(String machineName) {
	//		if (temp.getMachines().hasProperty(machineName)) {
	//			def machine = temp.getMachines().getElement(machineName)
	//			return new MachineModifier(machine, machine.getSees(), machine.getRefines())
	//		}
	//	}
	//
	//	/**
	//	 * Finds the context with the specified name and returns a {@link ContextModifier} object
	//	 * to allow the modification of the context elements.
	//	 * @param contextName of the context to be modified
	//	 * @return a {@link ContextModifier} object to allow the modification of context with name
	//	 * 	contextName or <code>null</code>, if the specified context does not exist
	//	 */
	//	def ContextModifier getContext(String contextName) {
	//		if (temp.getContexts().hasProperty(contextName)) {
	//			def ctx = temp.getContexts().getElement(contextName)
	//			return new ContextModifier(ctx, ctx.getExtends())
	//		}
	//	}
}
