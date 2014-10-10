package de.prob.model.eventb

import de.prob.Main
import de.prob.animator.command.GetCurrentPreferencesCommand
import de.prob.model.eventb.theory.Theory
import de.prob.model.representation.Axiom
import de.prob.model.representation.BSet
import de.prob.model.representation.Constant
import de.prob.model.representation.Invariant
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.RefType
import de.prob.model.representation.RefType.ERefType
import de.prob.scripting.Api
import de.prob.scripting.EventBFactory

public class ModelModifier {

	EventBModel temp
	Map<String, String> prefs
	boolean loadByDefault

	def ModelModifier(EventBModel model) {
		temp = deepCopy(model)
		GetCurrentPreferencesCommand cmd = new GetCurrentPreferencesCommand()
		model.getStateSpace().execute(cmd)
		prefs = cmd.getPreferences()
		Api api = Main.getInjector().getInstance(Api.class)
		loadByDefault = api.loadVariablesByDefault
	}

	public static EventBModel deepCopy(EventBModel model) {
		EventBFactory factory = Main.getInjector().getInstance(EventBFactory.class)
		EventBModel newModel = factory.modelProvider.get()

		def mainComp = deepCopy(newModel, model.getMainComponent())
		newModel.addTheories(new ModelElementList<Theory>(model.getChildrenOfType(Theory.class)))
		newModel.setMainComponent(mainComp)
		newModel.setModelFile(model.getModelFile())
		newModel
	}

	public static Context deepCopy(EventBModel model, Context context) {
		if (model.getComponents().containsKey(context.getName())) {
			return model.getComponents().get(context.getName())
		}
		def newContext = new Context(context.name, context.directoryPath)
		model.addContext(newContext)

		def Extends = context.Extends.collect { deepCopy(model, it) }
		Extends.each {
			model.addRelationship(newContext.getName(), it.getName(), new RefType(ERefType.EXTENDS))
		}
		newContext.addExtends(new ModelElementList<Context>(Extends))

		newContext.addSets(new ModelElementList<BSet>(context.sets))

		newContext.addConstants(new ModelElementList<Constant>(context.constants))
		newContext.addProofs(new ModelElementList<ProofObligation>(context.proofs))

		def axioms = context.axioms
		def inherited = context.getChildrenOfType(Axiom.class).findAll {
			!axioms.contains(it)
		}
		newContext.addAxioms(axioms, new ModelElementList<EventBAxiom>(inherited))
		newContext
	}

	public static EventBMachine deepCopy(EventBModel model, EventBMachine machine) {
		if (model.getComponents().containsKey(machine.getName())) {
			return model.getComponents().get(machine.getName())
		}
		def newMachine = new EventBMachine(machine.name, machine.directoryPath)
		model.addMachine(newMachine)

		def refines = machine.refines.collect { deepCopy(model, it) }
		refines.each {
			model.addRelationship(newMachine.getName(), it.getName(), new RefType(ERefType.REFINES))
		}
		newMachine.addRefines(new ModelElementList<EventBMachine>(refines))

		def sees = machine.sees.collect { deepCopy(model, it) }
		sees.each {
			model.addRelationship(newMachine.getName(), it.getName(), new RefType(ERefType.SEES))
		}
		newMachine.addSees(new ModelElementList<Context>(sees))

		newMachine.addVariables(new ModelElementList<EventBVariable>(machine.variables))

		def invariants = machine.invariants
		def inherited = machine.getChildrenOfType(Invariant.class).findAll {
			!invariants.contains(it)
		}
		newMachine.addInvariants(invariants, new ModelElementList<EventBInvariant>(inherited))

		newMachine.addVariant(new ModelElementList<Variant>(machine.getChildrenOfType(Variant.class)))

		newMachine.addProofs(new ModelElementList<ProofObligation>(machine.proofs))

		def events = machine.events.collect { deepCopy(model, newMachine, it) }
		newMachine.addEvents(new ModelElementList<Event>(events))
		newMachine
	}

	public static Event deepCopy(EventBModel model, EventBMachine parentMachine, Event event) {
		def newEvent = new Event(parentMachine, event.name, event.type)

		def refines = event.refines.collect {
			def pMachine = model.getComponents().get(it.parentMachine.name)
			pMachine.getEvent(it.name)
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

	def EventBModel getModifiedModel() {
		temp.isFinished()
		EventBFactory.loadModel(temp, prefs, loadByDefault)
		return temp
	}

	def void changePreference(String prefName, String prefValue) {
		prefs[prefName] = prefValue
	}

	def loadVariables(boolean byDefault) {
		loadByDefault = byDefault
	}

	def MachineModifier getMachine(String machineName) {
		if (temp.getMachines().hasProperty(machineName)) {
			return new MachineModifier(temp.getMachines().getProperty(machineName))
		}
	}

	def ContextModifier getContext(String contextName) {
		if (temp.getContexts().hasProperty(contextName)) {
			return new ContextModifier(temp.getContexts().getProperty(contextName))
		}
	}
}
