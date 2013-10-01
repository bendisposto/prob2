package de.prob.model.eventb.translate;

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBAction
import de.prob.model.eventb.EventBAxiom
import de.prob.model.eventb.EventBConstant
import de.prob.model.eventb.EventBGuard
import de.prob.model.eventb.EventBInvariant
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBVariable
import de.prob.model.eventb.EventParameter
import de.prob.model.eventb.Variant
import de.prob.model.eventb.Witness
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.theory.Theory
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.BSet
import de.prob.model.representation.RefType
import de.prob.model.representation.RefType.ERefType
import edu.uci.ics.jung.graph.DirectedSparseMultigraph


public class EventBTranslator {

	def File modelFile
	def private String directoryPath
	def AbstractElement mainComponent
	def Map<String,AbstractElement> components = [:]
	def List<EventBMachine> machines = []
	def List<Context> contexts = []
	def List<Theory> theories = []
	def List<PO> proofInformation = []
	def Set<IFormulaExtension> typeEnv
	def private Map<String, Event> events = [:]
	def private ProofTranslator proofTranslator = new ProofTranslator()
	def DirectedSparseMultigraph<String,RefType> graph = new DirectedSparseMultigraph<String, RefType>()

	def EventBTranslator(String fileName) {
		long time = System.currentTimeMillis()
		modelFile = new File(fileName)
		long time1 = System.currentTimeMillis()

		int index = modelFile.getName().lastIndexOf('.')
		String name = index != -1 ? modelFile.getName().substring(0,index) : modelFile.getName()

		index = modelFile.getAbsolutePath().lastIndexOf('/')
		directoryPath = index != -1 ? modelFile.getAbsolutePath().substring(0, index) : modelFile.getAbsolutePath()
		println "Groovy String manipulation: "+ (System.currentTimeMillis() - time1)

		TheoryTranslator theoryTranslator = new TheoryTranslator()
		theories = theoryTranslator.getTheories(directoryPath)
		typeEnv = theoryTranslator.getExtensions()

		String baseFile = "${directoryPath}/${name}"
		println "Init Translator: "+(System.currentTimeMillis()-time)

		time = System.currentTimeMillis()
		mainComponent = extractComponent(name, getXML(modelFile), baseFile)
		println "Extracting Component: "+(System.currentTimeMillis()-time)
	}

	def Node getXML(file) {
		long time = System.currentTimeMillis()
		String text = file.text.replaceAll("org.eventb.core.","")
		Node parser = new XmlParser().parseText(text)
		println "Extracted "+file.absolutePath+" in: "+(System.currentTimeMillis() - time)
		return parser
	}

	def AbstractElement extractComponent(name, xml, baseFile) {
		String xmlName = xml.name()
		if(xmlName == "contextFile") {
			return extractContext(name, xml, baseFile);
		} else if(xmlName == "machineFile") {
			return extractMachine(name, xml, baseFile);
		}
	}

	def extractContext(name, xml, baseFile) {
		long time = System.currentTimeMillis()
		graph.addVertex(name)

		Context context = new Context(name)
		List<Context> extendedContexts = []
		xml.extendsContext.'@target'.each {
			if(components.containsKey(it)) {
				extendedContexts.add(components[it])
			} else {
				File f = new File("${directoryPath}/${it}.buc")
				String newBaseFile = "${directoryPath}/${it}"
				extendedContexts.add(extractContext(it,getXML(f),newBaseFile))
			}
			graph.addEdge(new RefType(ERefType.EXTENDS), name, it)
		}
		context.addExtends(extendedContexts)

		List<BSet> sets = xml.carrierSet.'@identifier'.collect { new BSet(it) }
		context.addSets(sets)

		List<EventBAxiom> axioms = xml.axiom.collect {
			new EventBAxiom(it.@label, it.@predicate, it.@theorem == "true", typeEnv)
		}
		context.addAxioms(axioms)

		List<EventBConstant> constants = xml.constant.collect {
			new EventBConstant(it.@identifier, it.'@de.prob.symbolic.symbolicAttribute' == "true")
		}
		context.addConstants(constants)

		proofInformation.addAll(proofTranslator.translateProofsForContext(context, baseFile))

		components[name] = context
		contexts.add(context)
		println "Extracted Context "+context.getName()+" in: "+(System.currentTimeMillis() - time)
		return context
	}

	def extractMachine(name, xml, baseFile) {

		long time = System.currentTimeMillis()
		graph.addVertex(name)

		EventBMachine machine = new EventBMachine(name)
		List<Context> sees = []
		xml.seesContext.'@target'.each {
			if(components.containsKey(it)) {
				sees.add(components[it])
			} else {
				File f = new File("${directoryPath}/${it}.buc")
				String newBaseFile = "${directoryPath}/${it}"
				sees.add(extractContext(it,getXML(f),newBaseFile))
			}
			graph.addEdge(new RefType(ERefType.SEES), name, it)
		}
		machine.addSees(sees)

		List<EventBMachine> refines = []
		xml.refinesMachine.'@target'.each {
			if(components.containsKey(it)) {
				refines.add(components[it])
			} else {
				File f = new File("${directoryPath}/${it}.bum")
				String newBaseFile = "${directoryPath}/${it}"
				refines.add(extractMachine(it,getXML(f),newBaseFile))
			}
			graph.addEdge(new RefType(ERefType.REFINES), name, it)
		}
		machine.addRefines(refines)

		List<EventBVariable> variables = xml.variable.'@identifier'.collect { new EventBVariable(it) }
		machine.addVariables(variables)

		List<EventBInvariant> invariants = xml.invariant.collect { new EventBInvariant(it.@label, it.@predicate, it.@theorem == "true", typeEnv) }
		machine.addInvariants(invariants)

		List<Variant> variant = xml.variant.'@expression'.collect { new Variant(it, typeEnv) }
		machine.addVariant(variant)

		List<Event> events = xml.event.collect { extractEvent(it) }
		machine.addEvents(events)

		proofInformation.addAll(new ProofTranslator().translateProofsForMachine(machine, baseFile))

		components[name] = machine
		machines.add(machine)
		println "Extracted Machine "+machine.getName()+" in: "+(System.currentTimeMillis() - time)
		return machine
	}

	def extractEvent(xml) {
		String name = xml.'@label'
		String convergence = xml.'@convergence'
		EventType eventType = convergence == "0" ? EventType.ORDINARY : (convergence == "1" ? EventType.CONVERGENT : EventType.ANTICIPATED);
		Event event = new Event(name, eventType)

		List<Event> refines = xml.refinesEvent.'@target'.collect { events[it] }
		event.addRefines(refines)

		List<EventBGuard> guards = xml.guard.collect {
			new EventBGuard(event, it.@label, it.@predicate, it.@theorem == "true", typeEnv)
		}
		event.addGuards(guards)

		List<EventBAction> actions = xml.action.collect {
			new EventBAction(event, it.@label, it.@assignment, typeEnv)
		}
		event.addActions(actions)

		List<Witness> witnesses = xml.witness.collect {
			Witness witness = new Witness(event, it.@label, it.@predicate, typeEnv)
		}
		event.addWitness(witnesses)

		List<EventParameter> parameters = xml.parameter.'@identifier'.collect { new EventParameter(event,it) }
		event.addParameters(parameters)

		events[name] = event
		return event
	}
}
