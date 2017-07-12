package de.prob.scripting

import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.classicalb.ClassicalBMachine
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.translate.EventBModelTranslator
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.model.representation.DependencyGraph.Edge
import de.prob.statespace.StateSpace

class LoadClosures {

	def static Closure<Object> EMPTY = {StateSpace s -> }

	def static Closure<Object> EVENTB =  {StateSpace s ->
		def toSubscribe = new ArrayList<IEvalElement>()
		def vars = new HashSet<String>()
		def mt = new EventBModelTranslator(s.getModel(), s.getMainComponent())
		mt.extractMachineHierarchy(s.getMainComponent(), s.getModel()).reverse().each {
			it.getVariables().each {
				if (!vars.contains(it.getName())) {
					toSubscribe << it.formula
				}
				vars << it.getName()
			}
		}
		def cs = new HashSet<String>()
		mt.extractContextHierarchy(s.getMainComponent(), s.getModel()).reverse().each {
			it.getConstants().each {
				if (!cs.contains(it.getName())) {
					toSubscribe << it.formula
				}
				cs << it.getName()
			}
		}
		s.subscribe(s, toSubscribe)
	}

	def static List<ClassicalBMachine> extractClassicalBHierarchy(ClassicalBModel model, String machineName) {
		ClassicalBMachine machine = model.getComponent(machineName)
		List<String> outEdges = model.getGraph().getOutEdges(machineName)
				.findAll { Edge e -> e.relationship == ERefType.USES || e.relationship == ERefType.INCLUDES }
				.collect { Edge e -> e.getTo().getElementName() }
		List<ClassicalBMachine> ms = [machine]
		List<ClassicalBMachine> referenced = outEdges.collect { String name ->
			extractClassicalBHierarchy(model, name)
		}.inject([]) { acc, list -> acc + list }
		ms.addAll(referenced)
		ms
	}

	def static Closure<Object> B = {StateSpace s ->
		List<ClassicalBMachine> machines = extractClassicalBHierarchy(s.getModel(), s.getModel().getMainMachine().getName())
		machines.each { ClassicalBMachine m ->
			s.subscribe(s, m.variables.collect {it.formula})
		}
	}
}
