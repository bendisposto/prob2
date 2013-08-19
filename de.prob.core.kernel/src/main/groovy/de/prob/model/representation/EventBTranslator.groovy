package de.prob.model.representation;

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
import de.prob.model.representation.RefType.ERefType
import edu.uci.ics.jung.graph.DirectedSparseMultigraph


public class EventBTranslator {

	def File modelFile
	def private directoryPath
	def AbstractElement mainComponent
	def Map<String,AbstractElement> components = [:]
	def List<Machine> machines = []
	def List<Context> contexts = []
	def private events = [:]
	def DirectedSparseMultigraph<String,RefType> graph = new DirectedSparseMultigraph<String, RefType>()

	def EventBTranslator(fileName) {
		modelFile = new File(fileName)
		def name = modelFile.getName().lastIndexOf('.').with {
			it != -1 ? modelFile.getName()[0..<it] : modelFile.getName()
		}
		directoryPath = modelFile.getAbsolutePath().lastIndexOf('/').with {
			it != -1 ? modelFile.getAbsolutePath()[0..<it] : modelFile.getAbsolutePath()
		}
		def proofFile = new File("${directoryPath}/${name}.bps")

		mainComponent = extractComponent(name, getXML(modelFile), getXML(proofFile))
	}

	def getXML(file) {
		def text = file.text.replaceAll("org.eventb.core.","")
		return new XmlParser().parseText(text)
	}

	def extractComponent(name, xml, proofs) {
		if(xml.name() == "contextFile") {
			return extractContext(name, xml, proofs);
		}
		if(xml.name() == "machineFile") {
			return extractMachine(name, xml, proofs);
		}
	}

	def extractContext(name, xml, proofs) {
		if(components.containsKey(name)) {
			return components[name]
		}
		graph.addVertex(name)

		def proofFactory = new ProofFactory(proofs)

		def context = new Context(name)
		def extendedContexts = []
		xml.extendsContext.'@target'.each {
			def f = new File("${directoryPath}/${it}.buc")
			def p = new File("${directoryPath}/${it}.bps")
			extendedContexts << extractContext(it,getXML(f),getXML(p))
			graph.addEdge(new RefType(ERefType.EXTENDS), name, it)
		}
		context.addExtends(extendedContexts)

		def sets = []
		xml.carrierSet.'@identifier'.each {
			sets << new BSet(it)
		}
		context.addSets(sets)

		def axioms = []
		xml.axiom.each {
			def label = it.'@label'
			def predicate = it.'@predicate'
			def theorem = it.'@theorem' == "true"
			def axiom = new EventBAxiom(label, predicate, theorem)
			axioms << axiom
			proofFactory.addProof(label, axiom)
		}
		context.addAxioms(axioms)

		def constants = []
		xml.constant.'@identifier'.each {
			constants << new EventBConstant(it)
		}
		context.addConstants(constants)

		context.setDischarged(proofFactory.getDischarged())
		context.setUnproven(proofFactory.getUnproven())
		components[name] = context
		contexts << context
		return context
	}

	def extractMachine(name, xml, proofs) {
		if(components.containsKey(name)) {
			return components[name]
		}
		graph.addVertex(name)

		def proofFactory = new ProofFactory(proofs)

		def machine = new EventBMachine(name)
		def sees = []
		xml.seesContext.'@target'.each {
			def f = new File("${directoryPath}/${it}.buc")
			def p = new File("${directoryPath}/${it}.bps")
			sees << extractContext(it,getXML(f),getXML(p))
			graph.addEdge(new RefType(ERefType.SEES), name, it)
		}
		machine.addSees(sees)

		def refines = []
		xml.refinesMachine.'@target'.each {
			def f = new File("${directoryPath}/${it}.bum")
			def p = new File("${directoryPath}/${it}.bps")
			refines << extractMachine(it,getXML(f),getXML(p))
			graph.addEdge(new RefType(ERefType.REFINES), name, it)
		}
		machine.addRefines(refines)

		def variables = []
		xml.variable.'@identifier'.each {
			variables << new EventBVariable(it)
		}
		machine.addVariables(variables)

		def invariants = []
		xml.invariant.each {
			def label = it.'@label'
			def predicate = it.'@predicate'
			def theorem = it.'@theorem' == "true"
			def invariant = new EventBInvariant(label, predicate, theorem)
			invariants << invariant
			proofFactory.addProof(label, invariant)
		}
		machine.addInvariants(invariants)

		def variant = []
		xml.variant.'@expression'.each {
			variant << new Variant(it)
		}
		machine.addVariant(variant)

		def events = []
		xml.event.each { events << extractEvent(it, proofFactory) }
		machine.addEvents(events)

		proofFactory.addInvariantProofs(invariants, events)

		machine.setDischarged(proofFactory.getDischarged())
		machine.setUnproven(proofFactory.getUnproven())
		components[name] = machine
		machines << machine
		return machine
	}

	def extractEvent(xml, ProofFactory proofFactory) {
		def name = xml.'@label'
		def convergence = xml.'@convergence'
		def eventType = convergence == "0" ? EventType.ORDINARY : (convergence == "1" ? EventType.CONVERGENT : EventType.ANTICIPATED);
		def event = new Event(name, eventType)

		def refines = []
		xml.refinesEvent.'@target'.each { refines << events[it] }
		event.addRefines(refines)

		def guards = []
		xml.guard.each {
			def label = it.'@label'
			def predicate = it.'@predicate'
			def theorem = it.'@theorem' == "true"
			def guard = new EventBGuard(label, predicate, theorem)
			guards << guard
			proofFactory.addProof(name,label,guard)
		}
		event.addGuards(guards)

		def actions = []
		xml.action.each {
			def label = it.'@label'
			def assignment = it.'@assignment'
			def action = new EventBAction(label, assignment)
			actions << action
			proofFactory.addProof(name,label,action)
		}
		event.addActions(actions)

		def witnesses = []
		xml.witness.each {
			def label = it.'@label'
			def predicate = it.'@predicate'
			def witness = new Witness(label, predicate)
			witnesses << witness
			proofFactory.addProof(name, label, witness)
		}
		event.addWitness(witnesses)

		def parameters = []
		xml.parameter.'@identifier'.each { parameters << new EventParameter() }
		event.addParameters(parameters)

		events[name] = event
		return event
	}
}
