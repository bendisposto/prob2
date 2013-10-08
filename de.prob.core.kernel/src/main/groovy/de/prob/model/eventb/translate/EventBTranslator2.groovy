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
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.RefType
import de.prob.model.representation.RefType.ERefType
import edu.uci.ics.jung.graph.DirectedSparseMultigraph


public class EventBTranslator2 {

	def File modelFile
	def private String directoryPath
	def AbstractElement mainComponent
	def Map<String,AbstractElement> components = [:]
	def List<EventBMachine> machines = new ModelElementList<EventBMachine>()
	def List<Context> contexts = new ModelElementList<Context>()
	def List<Theory> theories = []
	def Set<IFormulaExtension> typeEnv
	def private Map<String, Event> events = [:]
	def private ProofTranslator proofTranslator = new ProofTranslator()
	def private XmlParser parser = new XmlParser()
	def DirectedSparseMultigraph<String,RefType> graph = new DirectedSparseMultigraph<String, RefType>()
	def private Map<String, EventBInvariant> invCache = [:]
	def private Map<String, EventBAxiom> axmCache = [:]

	def EventBTranslator2(String fileName) {
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

		Context context = new Context(name, directoryPath)
		List<Context> extendedContexts = new ModelElementList<Context>()
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
		context.addSets(new ModelElementList<BSet>(sets))

		List<EventBAxiom> axioms = new ModelElementList<EventBAxiom>()
		List<EventBAxiom> inherited	 = []
		xml.scAxiom.each {
			def source = it.@source
			def internalName = source.substring(source.lastIndexOf('#')+1,source.size())
			if(source.contains("contextFile#"+name)) {
				def axm = new EventBAxiom(it.@label, it.@predicate, it.@theorem == "true", typeEnv)
				axioms.add(axm)
				axmCache[internalName] = axm
			} else {
				inherited.add(axmCache[internalName])
			}
		}
		context.addAxioms(axioms, inherited)

		List<EventBConstant> constants = xml.scConstant.collect {
			new EventBConstant(it.@name, it.'@de.prob.symbolic.symbolicAttribute' == "true")
		}
		context.addConstants(new ModelElementList<EventBConstant>(constants))

		context.addProofs(proofTranslator.translateProofsForContext(context, baseFile))

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

		EventBMachine machine = new EventBMachine(name, directoryPath)
		List<Context> sees = new ModelElementList<Context>()
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

		List<EventBMachine> refines = new ModelElementList<EventBMachine>()
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
		machine.addVariables(new ModelElementList<EventBVariable>(variables))

		List<EventBInvariant> invariants = new ModelElementList<EventBInvariant>()
		List<EventBInvariant> inherited = []
		xml.scInvariant.each {
			def source = it.@source
			def internalName = source.substring(source.lastIndexOf('#')+1,source.size())
			if(source.contains("machineFile#"+name)) {
				def inv = new EventBInvariant(it.@label, it.@predicate, it.@theorem == "true", typeEnv)
				invariants.add(inv)
				invCache[internalName] = inv
			} else {
				inherited.add(invCache[internalName])
			}
		}
		machine.addInvariants(invariants, inherited)

		List<Variant> variant = xml.scVariant.'@expression'.collect {
			new Variant(it, typeEnv)
		}
		machine.addVariant(variant)

		List<Event> events = xml.scEvent.collect { extractEvent(it) }
		machine.addEvents(new ModelElementList<Event>(events))

		machine.addProofs(new ProofTranslator().translateProofsForMachine(machine, baseFile))

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
		event.addRefines(new ModelElementList<Event>(refines))

		List<EventBGuard> guards = xml.scGuard.collect {
			new EventBGuard(event, it.@label, it.@predicate, it.@theorem == "true", typeEnv)
		}
		event.addGuards(new ModelElementList<EventBGuard>(guards))

		List<EventBAction> actions = xml.scAction.collect {
			new EventBAction(event, it.@label, it.@assignment, typeEnv)
		}
		event.addActions(new ModelElementList<EventBAction>(actions))

		List<Witness> witnesses = xml.scWitness.collect {
			new Witness(event, it.@label, it.@predicate, typeEnv)
		}
		event.addWitness(new ModelElementList<Witness>(witnesses))

		List<EventParameter> parameters = xml.scParameter.@name.collect {
			new EventParameter(event,it)
		}
		event.addParameters(new ModelElementList<EventParameter>(parameters))

		events[rodinCrazyInternalName] = event
		return event
	}
}
