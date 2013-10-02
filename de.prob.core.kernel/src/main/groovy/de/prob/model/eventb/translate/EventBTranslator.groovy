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
	def private XmlParser parser = new XmlParser()
	def DirectedSparseMultigraph<String,RefType> graph = new DirectedSparseMultigraph<String, RefType>()

	def EventBTranslator(String fileName) {
		modelFile = new File(fileName)

		int index = modelFile.getName().lastIndexOf('.')
		String name = index != -1 ? modelFile.getName().substring(0,index) : modelFile.getName()

		index = modelFile.getAbsolutePath().lastIndexOf('/')
		directoryPath = index != -1 ? modelFile.getAbsolutePath().substring(0, index) : modelFile.getAbsolutePath()

		TheoryTranslator theoryTranslator = new TheoryTranslator()
		theories = theoryTranslator.getTheories(directoryPath)
		typeEnv = theoryTranslator.getExtensions()

		String baseFile = "${directoryPath}/${name}"

		mainComponent = extractComponent(name, getXML(modelFile), baseFile)
	}

	def Node getXML(file) {
		String text = file.text.replaceAll("org.eventb.core.","")
		Node parser = parser.parseText(text)
		return parser
	}

	def AbstractElement extractComponent(name, xml, baseFile) {
		String xmlName = xml.name()
		if(xmlName == "scContextFile") {
			return extractContext(name, xml, baseFile);
		} else if(xmlName == "scMachineFile") {
			return extractMachine(name, xml, baseFile);
		}
	}

	def extractContext(name, xml, baseFile) {
		long time = System.currentTimeMillis()
		graph.addVertex(name)

		xml.scInternalContext.each {
			def ctx = it.@name
			if(!components.containsKey(ctx)) {
				def newBaseFile = directoryPath + "/" + ctx
				extractContext(ctx, it, newBaseFile)
			}
		}

		Context context = new Context(name)
		List<Context> extendedContexts = []
		xml.scExtendsContext.'@scTarget'.each {
			def ctx = it.substring(it.lastIndexOf('#')+1,it.size())
			if(components.containsKey(ctx)) {
				extendedContexts.add(components[ctx])
			} else {
				File f = new File(directoryPath+"/"+ctx+".bcc")
				String newBaseFile = directoryPath+"/"+ctx
				extendedContexts.add(extractContext(ctx,getXML(f),newBaseFile))
			}
			graph.addEdge(new RefType(ERefType.EXTENDS), name, ctx)
		}
		context.addExtends(extendedContexts)

		List<BSet> sets = xml.scCarrierSet.@name.collect { new BSet(it) }
		context.addSets(sets)

		List<EventBAxiom> axioms = xml.scAxiom.findAll { it.@source.contains("contextFile#"+name) }.collect {
			new EventBAxiom(it.@label, it.@predicate, it.@theorem == "true", typeEnv)
		}
		context.addAxioms(axioms)

		List<EventBConstant> constants = xml.scConstant.collect {
			new EventBConstant(it.@name, it.'@de.prob.symbolic.symbolicAttribute' == "true")
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

		xml.scInternalContext.each {
			def ctx = it.@name
			if(!components.containsKey(ctx)) {
				def newBaseFile = directoryPath + "/" + ctx
				extractContext(ctx, it, newBaseFile)
			}
		}

		EventBMachine machine = new EventBMachine(name)
		List<Context> sees = []
		xml.scSeesContext.@scTarget.each {
			def ctx = it.substring(it.lastIndexOf('/')+1,it.indexOf(".bcc"))
			if(components.containsKey(ctx)) {
				sees.add(components[ctx])
			} else {
				File f = new File(directoryPath+"/"+ctx+".bcc")
				String newBaseFile = directoryPath+"/"+ctx
				sees.add(extractContext(ctx,getXML(f),newBaseFile))
			}
			graph.addEdge(new RefType(ERefType.SEES), name, ctx)
		}
		machine.addSees(sees)

		List<EventBMachine> refines = []
		xml.scRefinesMachine.@scTarget.each {
			def mch = it.substring(it.lastIndexOf('/')+1,it.indexOf(".bcm"))
			if(components.containsKey(mch)) {
				refines.add(components[mch])
			} else {
				File f = new File(directoryPath+"/"+mch+".bcm")
				String newBaseFile = directoryPath+"/"+mch
				refines.add(extractMachine(mch,getXML(f),newBaseFile))
			}
			graph.addEdge(new RefType(ERefType.REFINES), name, mch)
		}
		machine.addRefines(refines)

		List<EventBVariable> variables = xml.scVariable.@name.collect { new EventBVariable(it) }
		machine.addVariables(variables)

		List<EventBInvariant> invariants = []
		xml.scInvariant.findAll { it.@source.contains("machineFile#"+name) }.collect { new EventBInvariant(it.@label, it.@predicate, it.@theorem == "true", typeEnv)}
		machine.addInvariants(invariants)

		List<Variant> variant = xml.scVariant.'@expression'.collect {
			new Variant(it, typeEnv)
		}
		machine.addVariant(variant)

		List<Event> events = xml.scEvent.collect { extractEvent(it) }
		machine.addEvents(events)

		proofInformation.addAll(new ProofTranslator().translateProofsForMachine(machine, baseFile))

		components[name] = machine
		machines.add(machine)
		println "Extracted Machine "+machine.getName()+" in: "+(System.currentTimeMillis() - time)
		return machine
	}

	def extractEvent(xml) {
		String rodinCrazyInternalName = xml.@name
		String name = xml.@label
		String convergence = xml.@convergence
		EventType eventType = convergence == "0" ? EventType.ORDINARY : (convergence == "1" ? EventType.CONVERGENT : EventType.ANTICIPATED);
		Event event = new Event(name, eventType)

		List<Event> refines = xml.scRefinesEvent.@scTarget.collect {
			def internalName = it.substring(it.lastIndexOf('#')+1,it.size())
			events[internalName]
		}
		event.addRefines(refines)

		List<EventBGuard> guards = xml.scGuard.collect {
			new EventBGuard(event, it.@label, it.@predicate, it.@theorem == "true", typeEnv)
		}
		event.addGuards(guards)

		List<EventBAction> actions = xml.scAction.collect {
			new EventBAction(event, it.@label, it.@assignment, typeEnv)
		}
		event.addActions(actions)

		List<Witness> witnesses = xml.scWitness.collect {
			Witness witness = new Witness(event, it.@label, it.@predicate, typeEnv)
		}
		event.addWitness(witnesses)

		List<EventParameter> parameters = xml.scParameter.@name.collect {
			new EventParameter(event,it)
		}
		event.addParameters(parameters)

		events[rodinCrazyInternalName] = event
		return event
	}
}
