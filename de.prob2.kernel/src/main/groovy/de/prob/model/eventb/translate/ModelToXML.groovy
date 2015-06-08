package de.prob.model.eventb.translate

import groovy.xml.MarkupBuilder
import de.prob.model.eventb.Context
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine

public class ModelToXML {

	public ctr = 0;
	def ModelToXML() {
		ctr = 0
	}

	def String genName() {
		return "n" + ctr++;
	}

	def extractMachine(EventBMachine m) {
		StringWriter writer = new StringWriter();
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
				'org.eventb.core.predicate': it.getPredicate().toUnicode())
			}
			m.events.each { extractEvent(xml, it) }
		}

		return writer.toString();
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

	def extractContext(Context c) {
		StringWriter writer = new StringWriter();
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
				'org.eventb.core.predicate': it.getPredicate().toUnicode())
			}
		}
		return writer.toString()
	}
}
