package de.prob.model.eventb

import de.prob.Main
import de.prob.animator.command.GetCurrentPreferencesCommand
import de.prob.model.eventb.theory.Theory
import de.prob.model.representation.Set
import de.prob.model.representation.Constant
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.scripting.Api
import de.prob.scripting.EventBFactory
import de.prob.scripting.LoadClosures

public class ModelModifier {

	EventBModel temp
	Map<String, String> prefs
	Closure loader
	boolean startProB

	/**
	 * Creates an interface to allow the user to mutate the model object.
	 * The user can also specify an additional parameter 'startProB' which will
	 * determine if a ProB instance will be bound to the new
	 * model class. If not, a ProB instance can be lazily created later by calling
	 * the getStateSpace() method on the model object.
	 * @param model to be copied
	 * @param startProB default = true
	 */
	def ModelModifier(EventBModel model, boolean startProB=true) {
		temp = deepCopy(model)
		this.startProB = startProB
		if (startProB) {
			GetCurrentPreferencesCommand cmd = new GetCurrentPreferencesCommand()
			model.getStateSpace().execute(cmd)
			prefs = cmd.getPreferences()
			Api api = Main.getInjector().getInstance(Api.class)
			loader = api.getSubscribeClosure(LoadClosures.EVENTB)
		}
	}

	/**
	 * @param model that is to be copied
	 * @param startProB true, if the user wants to begin an instance of ProB when creating a model, false otherwise.
	 * @return a deep copy of the model and all model elements
	 */
	public static EventBModel deepCopy(EventBModel model) {
		EventBFactory factory = Main.getInjector().getInstance(EventBFactory.class)
		EventBModel newModel = factory.modelCreator.get()

		def mainComp = deepCopy(newModel, model.getMainComponent())
		newModel.addTheories(new ModelElementList<Theory>(model.getChildrenOfType(Theory.class)))
		newModel.setMainComponent(mainComp)
		newModel.setModelFile(model.getModelFile())
		newModel
	}

	/**
	 * Performs a deep copy of the specified {@link Context} object, and
	 * adds the object to the dependency graph in the {@link EventBModel}.
	 * If the {@link Context} object already exists in the model,
	 * this is simply retrieved from the model (because that means that
	 * a deep copy has already been made).
	 *
	 * @param model that provides the scope of the context
	 * @param context that is to be copied
	 * @return a deep copy of the context
	 */
	public static Context deepCopy(EventBModel model, Context context) {
		if (model.getComponents().containsKey(context.getName())) {
			return model.getComponents().get(context.getName())
		}
		def newContext = new Context(context.name, context.directoryPath)
		model.addContext(newContext)

		def Extends = context.Extends.collect { deepCopy(model, it) }
		Extends.each {
			model.addRelationship(newContext.getName(), it.getName(), ERefType.EXTENDS)
		}
		newContext.addExtends(new ModelElementList<Context>(Extends))

		newContext.addSets(new ModelElementList<Set>(context.sets))

		newContext.addConstants(new ModelElementList<Constant>(context.constants))
		newContext.addProofs(new ModelElementList<ProofObligation>(context.proofs))

		def axioms = context.axioms
		def inherited = context.allAxioms.findAll {
			!axioms.contains(it)
		}
		newContext.addAxioms(new ModelElementList<EventBAxiom>(axioms), new ModelElementList<EventBAxiom>(inherited))
		newContext
	}

	/**
	 * Performs a deep copy of the specified {@link EventBMachine} object, and
	 * adds the object to the dependency graph in the {@link EventBModel}.
	 * If the {@link EventBMachine} object already exists in the model,
	 * this is simply retrieved from the model (because that means that
	 * a deep copy has already been made).
	 *
	 * @param model that provides the scope of the machine
	 * @param machine that is to be copied
	 * @return a deep copy of the machine
	 */
	public static EventBMachine deepCopy(EventBModel model, EventBMachine machine) {
		if (model.getComponents().containsKey(machine.getName())) {
			return model.getComponents().get(machine.getName())
		}
		def newMachine = new EventBMachine(machine.name, machine.directoryPath)
		model.addMachine(newMachine)

		def refines = machine.refines.collect { deepCopy(model, it) }
		refines.each {
			model.addRelationship(newMachine.getName(), it.getName(), ERefType.REFINES)
		}
		newMachine.addRefines(new ModelElementList<EventBMachine>(refines))

		def sees = machine.sees.collect { deepCopy(model, it) }
		sees.each {
			model.addRelationship(newMachine.getName(), it.getName(), ERefType.SEES)
		}
		newMachine.addSees(new ModelElementList<Context>(sees))

		newMachine.addVariables(new ModelElementList<EventBVariable>(machine.variables))

		def invariants = machine.invariants
		def inherited = machine.allInvariants.findAll {
			!invariants.contains(it)
		}
		newMachine.addInvariants(new ModelElementList<EventBInvariant>(invariants), new ModelElementList<EventBInvariant>(inherited))

		newMachine.addVariant(new ModelElementList<Variant>(machine.getChildrenOfType(Variant.class)))

		newMachine.addProofs(new ModelElementList<ProofObligation>(machine.proofs))

		def events = machine.events.collect { deepCopy(newMachine, it) }
		newMachine.addEvents(new ModelElementList<Event>(events))
		newMachine
	}

	/**
	 * Perform a deep copy of an event. Performed via {@link ModelModifier#cloneEvent(EventBMachine, Event, String)}
	 * @param parentMachine of the specified event
	 * @param event that is to be copied
	 * @return a deep copy of the event in question
	 */
	public static Event deepCopy(EventBMachine parentMachine, Event event) {
		cloneEvent(parentMachine, event, event.getName())
	}

	/**
	 * This method performs a deep copy of an event, but the cloned event has a new
	 * name that is specified by the user (useful when creating mutant events that
	 * differ from the original event in small ways without actually deleting the
	 * original event).
	 * @param parentMachine of the specified event
	 * @param event that is to be copied
	 * @param newName of the cloned event
	 * @return Event object created when cloning the given event
	 */
	public static Event cloneEvent(EventBMachine parentMachine, Event event, String newName) {
		def newEvent = new Event(parentMachine, newName, event.type)

		def refines = event.refines.collect {
			it.parentMachine.getEvent(it.name)
		}

		newEvent.addRefines(new ModelElementList<Event>(refines))

		def guards = event.guards.collect {
			new EventBGuard(newEvent, it.name, it.predicate.getCode(), it.theorem, it.predicate.types)
		}
		newEvent.addGuards(new ModelElementList<EventBGuard>(guards))

		def actions = event.actions.collect {
			new EventBAction(newEvent, it.name, it.getCode().code, it.getCode().types)
		}
		newEvent.addActions(new ModelElementList<EventBAction>(actions))

		def witnesses = event.witnesses.collect {
			new Witness(newEvent, it.name, it.predicate.getCode(), it.predicate.types)
		}
		newEvent.addWitness(new ModelElementList<Witness>(witnesses))

		def parameters = event.parameters.collect { new EventParameter(newEvent, it.name) }
		newEvent.addParameters(new ModelElementList<EventParameter>(parameters))

		newEvent
	}

	/**
	 * This method makes the model object currently being modified into an
	 * immutable form.
	 * @return EventBModel object created
	 */
	def EventBModel getModifiedModel() {
		temp.isFinished()
		if (startProB) {
			EventBFactory.loadModel(temp, prefs, loader)
		}
		return temp
	}

	/**
	 * Change a given preference for the model in question
	 * @param prefName the name of the preference
	 * @param prefValue the new value
	 */
	def void changePreference(String prefName, String prefValue) {
		prefs[prefName] = prefValue
	}

	/**
	 * Specify which formulas are of interest by setting the closure that is to subscribe
	 * them
	 * @param loader that will load the variables of interest.
	 */
	def void setLoader(Closure loader) {
		this.loader = loader
	}

	/**
	 * Finds the machine with the specified name and returns a {@link MachineModifier} object
	 * to allow the modification of the machine elements.
	 * @param machineName of the machine to be modified
	 * @return a {@link MachineModifier} object to allow the modification of machine with name
	 * 	machineName or <code>null</code>, if the specified machine does not exist
	 */
	def MachineModifier getMachine(String machineName) {
		if (temp.getMachines().hasProperty(machineName)) {
			return new MachineModifier(temp.getMachines().getElement(machineName))
		}
	}

	/**
	 * Finds the context with the specified name and returns a {@link ContextModifier} object
	 * to allow the modification of the context elements.
	 * @param contextName of the context to be modified
	 * @return a {@link ContextModifier} object to allow the modification of context with name
	 * 	contextName or <code>null</code>, if the specified context does not exist
	 */
	def ContextModifier getContext(String contextName) {
		if (temp.getContexts().hasProperty(contextName)) {
			return new ContextModifier(temp.getContexts().getElement(contextName))
		}
	}
}
