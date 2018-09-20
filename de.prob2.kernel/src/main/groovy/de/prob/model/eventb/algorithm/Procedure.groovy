package de.prob.model.eventb.algorithm

import de.prob.model.representation.Named

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.AbstractModifier
import de.prob.model.eventb.Context
import de.prob.model.eventb.ContextModifier
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventModifier
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelGenerationException
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.representation.ModelElementList

class Procedure extends AbstractModifier implements Named {

	public static String CONTEXT_SUFFIX = "_ctx";
	public static String ABSTRACT_SUFFIX = "_abs";
	public static String IMPL_SUFFIX = "_impl";

	String name
	ModelElementList<String> arguments
	ModelElementList<String> results
	ContextModifier contextM
	MachineModifier absM
	MachineModifier concreteM
	EventModifier eventM
	EventB precondition
	EventB postcondition

	def Procedure(String name, Context seen, Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
		this.name = name
		this.arguments = new ModelElementList<String>()
		this.results = new ModelElementList<String>()
		def ctxM = new ContextModifier(new Context(name+CONTEXT_SUFFIX), typeEnvironment)
		this.contextM = seen ? ctxM.setExtends(seen) : ctxM
		def sees = new ModelElementList<Context>([contextM.getContext()])
		this.absM = new MachineModifier(new EventBMachine(name+ABSTRACT_SUFFIX), typeEnvironment).setSees(sees).var("apc", [grd_apc: "apc : NAT"], [act_apc: "apc := 0"])
		this.concreteM = new MachineModifier(new EventBMachine(name+IMPL_SUFFIX), typeEnvironment).setSees(sees).setRefines(absM.getMachine())
		this.eventM = new EventModifier(new Event(name, EventType.ORDINARY, false), false, typeEnvironment).guard("grd_apc", "apc = 0").action("act_apc", "apc := 1")
	}

	def Procedure(String name, Set<IFormulaExtension> typeEnv, ModelElementList<String> arguments,
	ModelElementList<String> results, ContextModifier contextM, MachineModifier absM,
	MachineModifier concreteM,	EventModifier eventM,
	EventB precondition, EventB postcondition) throws ModelGenerationException{
		super(typeEnv)
		this.name = name
		this.arguments = arguments
		this.results = results
		this.contextM = contextM
		this.absM = absM
		this.concreteM = concreteM
		this.eventM = eventM
		this.precondition = precondition
		this.postcondition = postcondition
	}

	def Procedure argument(LinkedHashMap definition) throws ModelGenerationException {
		def properties = validateProperties(definition, [name: String, type: String])
		return argument(properties["name"], properties["type"])
	}

	def Procedure argument(final String name, final String type) throws ModelGenerationException {
		ContextModifier cm = contextM.constant(name).axiom("typing_$name", "$name : $type")
		def sees = new ModelElementList<Context>([contextM.getContext()])
		def abstractM = absM.setSees(sees)
		def concM = concreteM.setSees(sees)
		return new Procedure(this.name, typeEnvironment, arguments.addElement(name), results, cm, abstractM, concM, eventM, precondition, postcondition)
	}

	def Procedure result(LinkedHashMap definition) throws ModelGenerationException {
		def properties = validateProperties(definition, [name: String, type: String])
		return result(properties["name"], properties["type"])
	}

	def Procedure result(String name, String type) throws ModelGenerationException {
		MachineModifier mm = absM.variable(name).invariant("typing_$name", "$name : $type")
				.initialisation({
					action("init_$name", "$name :: $type")
				})
		def concM = concreteM.setRefines(mm.getMachine())
		return new Procedure(this.name, typeEnvironment, arguments, results.addElement(name), contextM, mm, concM, eventM, precondition, postcondition)
	}

	def Procedure precondition(String precondition) throws ModelGenerationException {
		if (this.precondition) {
			throw new IllegalArgumentException("Precondition has already been set.")
		}
		EventB pre = parsePredicate(precondition)
		def em = eventM.guard("precondition", pre)
		def ctxM = contextM.axiom("precondition", precondition)
		def sees = new ModelElementList<Context>([contextM.getContext()])
		def abstractM = absM.setSees(sees)
		def concM = concreteM.setSees(sees)
		return new Procedure(this.name, typeEnvironment, arguments, results, ctxM, abstractM, concM, em, pre, postcondition)
	}

	def Procedure postcondition(String postcondition) throws ModelGenerationException {
		if (this.postcondition) {
			throw new IllegalArgumentException("Postcondition has already been set.")
		}
		EventB post = parsePredicate(postcondition)
		FormulaUtil fuu = new FormulaUtil()
		def input = contextM.getContext().getConstants().collect { it.getName() }
		if (contextM.getContext().getExtends()) {
			contextM.getContext().getExtends().each {
				input.addAll(it.getSets().collect { it.getName() })
				input.addAll(it.getConstants().collect { it.getName() })
			}
		}

		List<EventB> assignments = fuu.predicateToAssignments(post, input as Set, results as Set)
		def em = assignments.inject(eventM) { EventModifier evM, EventB a ->
			evM.action(a)
		}
		return new Procedure(this.name, typeEnvironment, arguments, results, contextM, absM, concreteM, em, precondition, post)
	}

	def Procedure implementation(Closure cls) throws ModelGenerationException {
		implementation(concreteM.make(cls))
	}

	def Procedure implementation(MachineModifier impl) {
		if (impl.getMachine().getChildrenOfType(Block.class).isEmpty()) {
			throw new IllegalArgumentException("the implementation of a procedure must define an algorithm")
		}
		def sees = new ModelElementList<Context>([contextM.getContext()])
		def mm = impl.setRefines(absM.getMachine()).setSees(sees)
		return new Procedure(this.name, typeEnvironment, arguments, results, contextM, absM, mm, eventM, precondition, postcondition)
	}

	def Context getContext() {
		contextM.getContext()
	}

	def EventBMachine getAbstractMachine() {
		absM.getMachine()
	}

	def EventBMachine getImplementation() {
		concreteM.getMachine()
	}

	def Event getEvent() {
		eventM.getEvent()
	}

	@Override
	public String toString() {
		def res = results.iterator().join(",")
		res = res + " := " + name + "("
		res = res + arguments.iterator().join(",")+ "):"
		res
	}

	def Procedure make(Closure definition) throws ModelGenerationException {
		Procedure proc = runClosure(definition)
		if (proc.precondition == null || proc.postcondition == null || proc.getImplementation().getChildrenOfType(Block.class).isEmpty()) {
			throw new IllegalArgumentException("procedure definition must define a precondition, postcondition, and algorithm for the implementation")
		}
		proc.finish()
	}

	def Procedure finish() {
		def procs = new ModelElementList<Procedure>([this])
		MachineModifier mm = new MachineModifier(getAbstractMachine().set(Procedure.class, procs))
		MachineModifier impl = new MachineModifier(getImplementation().set(Procedure.class, procs))
		return new Procedure(this.name, typeEnvironment, arguments, results, contextM, mm, impl, eventM, precondition, postcondition)
	}
}
