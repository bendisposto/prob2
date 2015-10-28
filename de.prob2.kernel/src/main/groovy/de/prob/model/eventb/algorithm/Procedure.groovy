package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.AbstractModifier
import de.prob.model.eventb.Event
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventModifier;
import de.prob.model.eventb.FormulaUtil;
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelGenerationException
import de.prob.model.representation.ModelElementList

class Procedure extends AbstractModifier {

	public static String ABSTRACT_SUFFIX = "_abs";
	public static String IMPL_SUFFIX = "_impl";

	class IdentifierInfo {
		String name
		String type
		String assignment

		def IdentifierInfo(String name, String type, String assignment) {
			this.name = name
			this.type = type
			this.assignment = assignment
		}
	}

	String name
	ModelElementList<IdentifierInfo> arguments
	ModelElementList<IdentifierInfo> results
	MachineModifier machineM
	Event abstractEvent
	EventB precondition
	EventB postcondition
	EventBMachine implementation

	def Procedure(String name, Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
		this.name = name
		this.arguments = new ModelElementList<IdentifierInfo>()
		this.results = new ModelElementList<IdentifierInfo>()
		this.machineM = new MachineModifier(new EventBMachine(name+ABSTRACT_SUFFIX))
	}

	def Procedure(String name, Set<IFormulaExtension> typeEnv, ModelElementList<IdentifierInfo> arguments,
	ModelElementList<IdentifierInfo> results, MachineModifier machineM, Event abstractE,
	EventB precondition, EventB postcondition, EventBMachine implementation) throws ModelGenerationException{
		super(typeEnv)
		this.name = name
		this.arguments = arguments
		this.results = results
		this.machineM = machineM
		this.abstractEvent = abstractE
		this.precondition = precondition
		this.postcondition = postcondition
		this.implementation = implementation
	}

	def Procedure argument(LinkedHashMap definition) {
		def properties = validateProperties(definition, [name: String, type: String, init: String])
		return argument(properties["name"], properties["type"], properties["init"])
	}

	def Procedure argument(final String name, final String type, final String init) {
		IdentifierInfo i = new IdentifierInfo(name, type, init)
		MachineModifier mm = machineM.variable(name).invariant("typing_$name", "$name : $type")
				.initialisation({ action("init_$name", init)})
		return new Procedure(this.name, typeEnvironment, arguments.addElement(i), results, mm, abstractEvent, precondition, postcondition, implementation)
	}

	def Procedure result(LinkedHashMap definition) {
		def properties = validateProperties(definition, [name: String, type: String, init: String])
		return result(properties["name"], properties["type"], properties["init"])
	}

	def Procedure result(String name, String type, String init) {
		IdentifierInfo i = new IdentifierInfo(name, type, init)
		MachineModifier mm = machineM.variable(name).invariant("typing_$name", "$name : $type")
				.initialisation({ action("init_$name", init)})
		return new Procedure(this.name, typeEnvironment, arguments, results.addElement(i), mm, abstractEvent, precondition, postcondition, implementation)
	}

	def Procedure abstraction(LinkedHashMap definition) {
		def properties = validateProperties(definition, [pre: String, post: String])
		return abstraction(properties["pre"], properties["post"])
	}

	def Procedure abstraction(String precondition, String postcondition) {
		EventModifier e = new EventModifier(new Event(name, EventType.ORDINARY, false))
		EventB pre = parsePredicate(precondition)
		EventB post = parseAssignment(postcondition)
		MachineModifier mm = machineM.var("apc", "apc : NAT", "apc := 0")
		e = e.guard("precondition", pre).guard("apc = 0").action("postcondition", post).action("apc := 1")
		//def mm = machineM.addEvent(e.getEvent())
		return new Procedure(name, typeEnvironment, arguments, results, mm, e.getEvent(), pre, post, implementation)
	}

	def Procedure algorithm(Map variables, Closure algorithm) {
		final FormulaUtil util = new FormulaUtil()
		MachineModifier mm = new MachineModifier(new EventBMachine(name+IMPL_SUFFIX)).setRefines(machineM.getMachine())
		ModelElementList<IdentifierInfo> ids = new ModelElementList<IdentifierInfo>(arguments).addMultiple(results)
		ids.each {
			mm = mm.variable(it.name).invariant("typing_${it.name}", "${it.name} : ${it.type}")
		}
		Set<String> varsWritten = new HashSet<String>()
		variables.each { final local, final global ->
			def assignment = util.copyVarAssignment(parseAssignment(ids.getElement(global).assignment), global, local)
			mm = mm.variable(local).invariant("typing_$local", "$local : ${ids.getElement(global).type}")
			mm = mm.initialisation {
				action("init_${global}_${local}", assignment)
			}
			varsWritten.add(global)
		}
		ids.each {
			if (!varsWritten.contains(it.name)) {
				mm = mm.initialisation { action "init_${it.name}", it.assignment }
			}
		}
		mm = mm.algorithm(algorithm)
		return new Procedure(name, typeEnvironment, arguments, results, machineM, abstractEvent, precondition, postcondition, mm.getMachine())
	}

	@Override
	public String toString() {
		def res = results.collect { v -> "${v.name}"}.iterator().join(",")
		res = res + " := " + name + "("
		res = res + arguments.collect { v -> "${v.name}" }.iterator().join(",")+ "):"
		res
	}

	def Procedure make(Closure definition) throws ModelGenerationException {
		Procedure proc = runClosure(definition)
		if (proc.abstractEvent == null || proc.implementation == null) {
			throw new IllegalArgumentException("procedure definition must define an abstraction and an implementation")
		}
		MachineModifier mm = new MachineModifier(proc.machineM.getMachine().set(Procedure.class, new ModelElementList<Procedure>([proc])))
		EventBMachine impl = proc.implementation.set(Procedure.class, new ModelElementList<Procedure>([proc]))
		return new Procedure(proc.name, typeEnvironment, proc.arguments, proc.results, mm, proc.abstractEvent, proc.precondition, proc.postcondition, impl)
	}
}
