package de.prob.model.eventb.translate

import java.io.File;

import groovy.xml.MarkupBuilder
import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel

public class ModelToXML {

	public ctr = 0;
	def ModelToXML() {
		ctr = 0
	}

	def String genName() {
		return "n" + ctr++;
	}
	
	def File writeToRodin(EventBModel model, String name, String path) {
		def directoryPath = path + File.separator + name
		def dir = createProjectFile(name, directoryPath)

		model.getComponents().each { k, v ->
			convert(v, directoryPath)
		}
		
		dir
	}

	def convert(EventBMachine m, String directoryPath) {
		extractMachine(m, directoryPath)
	}

	def convert(Context c, String directoryPath) {
		extractContext(c, directoryPath)
	}

	def extractMachine(EventBMachine m, String directoryPath) {
		String fileName = directoryPath + File.separator + m.getName() + ".bum"
		new File(fileName).withWriter("UTF-8") { writer ->
			MarkupBuilder xml = new MarkupBuilder(writer);

			xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8", standalone: "no")
			xml.'org.eventb.core.machineFile'('org.eventb.core.configuration': "org.eventb.core.fwd", version:"5") {
				m.sees.each {
					xml.'org.eventb.core.seesContext'(name: genName(), 'org.eventb.core.target': it.getName())
				}
				m.refines.each {
					xml.'org.eventb.core.refinesMachine'(name: genName(),
					'org.eventb.core.target': it.getName())
				}
				m.variables.each {
					xml.'org.eventb.core.variable'(name: genName(), 'org.eventb.core.identifier': it.getName())
				}
				if (m.variant) {
					xml.'org.eventb.core.variant'(name: genName(),
					'org.eventb.core.expression': m.variant.getExpression().toUnicode())
				}
				m.invariants.each {
					xml.'org.eventb.core.invariant'(name: genName(), 'org.eventb.core.label': it.getName(),
					'org.eventb.core.predicate': it.getPredicate().toUnicode(),
					'org.eventb.core.theorem': it.isTheorem())
				}
				m.events.each { extractEvent(xml, it) }
			}
		}
	}

	def extractEvent(MarkupBuilder xml, Event e) {
		def convergence = e.type == Event.EventType.ORDINARY ? "0"
				: e.type == Event.EventType.CONVERGENT ? "1"
				: "2"
		def extended = e.isExtended()
		xml.'org.eventb.core.event'(name: genName(),
		'org.eventb.core.convergence': convergence,
		'org.eventb.core.extended': extended,
		'org.eventb.core.label': e.getName()
		) {
			if (!e.getName().equals("INITIALISATION")) {
				e.refines.each {
					xml.'org.eventb.core.refinesEvent'(name: genName(),
					'org.eventb.core.target': it.getName())
				}
			}
			if (!extended) {
				e.parameters.each {
					xml.'org.eventb.core.parameter'(name: genName(),
					'org.eventb.core.identifier': it.getName())
				}
				e.guards.each {
					xml.'org.eventb.core.guard'(name: genName(),
					'org.eventb.core.label': it.getName(),
					'org.eventb.core.predicate': it.getPredicate().toUnicode(),
					'org.eventb.core.theorem': it.isTheorem())
				}
				e.witnesses.each {
					xml.'org.eventb.core.witness'(name: genName(),
						'org.eventb.core.label': it.getName(),
						'org.eventb.core.predicate': it.getPredicate().toUnicode())
				}
				e.actions.each {
					xml.'org.eventb.core.action'(name: genName(),
					'org.eventb.core.assignment': it.getCode().toUnicode(),
					'org.eventb.core.label': it.getName())
				}
			}
		}
	}

	def extractContext(Context c, String directoryPath) {
		String fileName = directoryPath + File.separator + c.getName() + ".buc"
		new File(fileName).withWriter("UTF-8") { writer ->
			MarkupBuilder xml = new MarkupBuilder(writer);

			xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8", standalone: "no")
			xml.'org.eventb.core.contextFile'('org.eventb.core.configuration': "org.eventb.core.fwd",
			version:"3") {
				c.Extends.each {
					xml.'org.eventb.core.extendsContext'(name: genName(),
					'org.eventb.core.target': it.getName())
				}
				c.sets.each {
					xml.'org.eventb.core.carrierSet'(name: genName(),
					'org.eventb.core.identifier': it.getFormula().toUnicode())
				}
				c.constants.each {
					xml.'org.eventb.core.constant'(name: genName(),
					'org.eventb.core.identifier': it.getFormula().toUnicode())
				}
				c.axioms.each {
					xml.'org.eventb.core.axiom'(name: genName(),
					'org.eventb.core.label': it.getName(),
					'org.eventb.core.predicate': it.getPredicate().toUnicode(),
					'org.eventb.core.theorem': it.isTheorem())
				}
			}
		}
	}

	def File createProjectFile(String modelName, String directoryPath) {
		def dir = new File(directoryPath)
		dir.mkdir()
		String projectFile = directoryPath + File.separator + ".project"
		def file = new File(projectFile)

		file.withWriter('UTF-8') { writer ->
			MarkupBuilder xml = new MarkupBuilder(writer);

			xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
			xml.setExpandEmptyElements(true)
			xml.projectDescription {
				name(modelName)
				comment()
				projects()
				buildSpec {
					buildCommand {
						name('org.rodinp.core.rodinbuilder')
						arguments()
					}
				}
				natures { nature('org.rodinp.core.rodinnature') }
			}
		}
		dir
	}
}
