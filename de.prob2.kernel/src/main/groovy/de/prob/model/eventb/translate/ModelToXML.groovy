package de.prob.model.eventb.translate

import groovy.xml.MarkupBuilder
import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.theory.Theory
import de.prob.model.representation.ElementComment
import de.prob.model.representation.ModelElementList

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

		extractTheories(model.getChildrenOfType(Theory.class), directoryPath)

		model.getMachines().each { m ->
			extractMachine(m, directoryPath)
		}

		model.getContexts().each { c ->
			extractContext(c, directoryPath)
		}

		dir
	}

	def extractTheories(ModelElementList<Theory> theories, String directoryPath) {
		if (theories.isEmpty()) {
			return
		}
		String fileName = directoryPath + File.separator + "TheoryPath.tul"
		Map<String, List<String>> collected = [:]
		theories.each { Theory t ->
			if (collected[t.parentDirectory]) {
				collected[t.parentDirectory] << t.name
			} else {
				collected[t.parentDirectory] = [t.name]
			}
		}
		new File(fileName).withWriter("UTF-8") { writer ->
			MarkupBuilder xml = new MarkupBuilder(writer);

			xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8", standalone: "no")
			xml.'org.eventb.theory.core.theoryLanguageRoot'('org.eventb.core.configuration': "org.eventb.theory.core.tul") {
				collected.each { dir, ts ->
					xml.'org.eventb.theory.core.availableTheoryProject'(name: genName(), 'org.eventb.theory.core.availableTheoryProject': File.separator+dir) {
						ts.each { t ->
							xml.'org.eventb.theory.core.availableTheory'(name: genName(),
							'org.eventb.theory.core.availableTheory': File.separator+dir+File.separator+t+".dtf|org.eventb.theory.core.deployedTheoryRoot#"+t
							)
						}
					}
				}
			}
		}
	}

	def extractMachine(EventBMachine m, String directoryPath) {
		String comment = m.getChildrenOfType(ElementComment.class).collect { it.getComment() }.iterator().join("\n")
		String fileName = directoryPath + File.separator + m.getName() + ".bum"
		new File(fileName).withWriter("UTF-8") { writer ->
			MarkupBuilder xml = new MarkupBuilder(writer);

			xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8", standalone: "no")
			xml.'org.eventb.core.machineFile'('org.eventb.core.configuration': "org.eventb.core.fwd", version:"5",
			'org.eventb.core.comment': comment) {
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
		String comment = e.getChildrenOfType(ElementComment.class).collect { it.getComment() }.iterator().join("\n")
		xml.'org.eventb.core.event'(name: genName(),
		'org.eventb.core.convergence': convergence,
		'org.eventb.core.extended': extended,
		'org.eventb.core.label': e.getName(),
		'org.eventb.core.comment': comment
		) {
			if (!e.getName().equals("INITIALISATION")) {
				e.getChildrenOfType(Event.class).each {
					xml.'org.eventb.core.refinesEvent'(name: genName(),
					'org.eventb.core.target': it.getName())
				}
			}
			e.parameters.each {
				xml.'org.eventb.core.parameter'(name: genName(),
				'org.eventb.core.identifier': it.getName(),
				'org.eventb.core.comment': it.getComment())
			}
			e.guards.each {
				xml.'org.eventb.core.guard'(name: genName(),
				'org.eventb.core.label': it.getName(),
				'org.eventb.core.predicate': it.getPredicate().toUnicode(),
				'org.eventb.core.theorem': it.isTheorem(),
				'org.eventb.core.comment': it.getComment())
			}
			e.witnesses.each {
				xml.'org.eventb.core.witness'(name: genName(),
				'org.eventb.core.label': it.getName(),
				'org.eventb.core.predicate': it.getPredicate().toUnicode(),
				'org.eventb.core.comment': it.getComment())
			}
			e.actions.each {
				xml.'org.eventb.core.action'(name: genName(),
				'org.eventb.core.assignment': it.getCode().toUnicode(),
				'org.eventb.core.label': it.getName(),
				'org.eventb.core.comment': it.getComment())
			}
		}
	}

	def extractContext(Context c, String directoryPath) {
		String comment = c.getChildrenOfType(ElementComment.class).collect { it.getComment() }.iterator().join("\n")
		String fileName = directoryPath + File.separator + c.getName() + ".buc"
		new File(fileName).withWriter("UTF-8") { writer ->
			MarkupBuilder xml = new MarkupBuilder(writer);

			xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8", standalone: "no")
			xml.'org.eventb.core.contextFile'('org.eventb.core.configuration': "org.eventb.core.fwd",
			version:"3", 'org.eventb.core.comment': comment) {
				c.getExtends().each {
					xml.'org.eventb.core.extendsContext'(name: genName(),
					'org.eventb.core.target': it.getName())
				}
				c.sets.each {
					xml.'org.eventb.core.carrierSet'(name: genName(),
					'org.eventb.core.identifier': it.getFormula().toUnicode(),
					'org.eventb.core.comment': it.getComment())
				}
				c.constants.each {
					xml.'org.eventb.core.constant'(name: genName(),
					'org.eventb.core.identifier': it.getFormula().toUnicode(),
					'org.eventb.core.comment': it.getComment())
				}
				c.axioms.each {
					xml.'org.eventb.core.axiom'(name: genName(),
					'org.eventb.core.label': it.getName(),
					'org.eventb.core.predicate': it.getPredicate().toUnicode(),
					'org.eventb.core.theorem': it.isTheorem(),
					'org.eventb.core.comment': it.getComment())
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
