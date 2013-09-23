package de.prob.model.eventb.translate

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog
import de.prob.model.eventb.Context
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.Machine
import de.prob.prolog.output.IPrologTermOutput

class EventBToPrologTranslator {
	EventBModel model
	List<MachineToAst> machineTranslators = []
	List<ContextToAst> contextTranslators = []

	def EventBToPrologTranslator(EventBModel model) {
		this.model = model
		model.getChildrenOfType(Context.class).each {
			contextTranslators << new ContextToAst(it)
		}
		model.getChildrenOfType(Machine.class).each {
			machineTranslators << new MachineToAst(it)
		}
	}

	def printProlog(IPrologTermOutput pto) {
		ASTProlog astPrinter = new ASTProlog(pto, null)

		pto.openTerm("load_event_b_project")

		pto.openList()
		machineTranslators.each {
			it.translateMachine().apply(astPrinter)
		}
		pto.closeList()

		pto.openList()
		contextTranslators.each {
			it.translateContext().apply(astPrinter)
		}
		pto.closeList()

		pto.openList()
		pto.openTerm("exporter_version")
		pto.printNumber(3)
		pto.closeTerm()

		machineTranslators.each { it.printProofsToProlog(pto) }
		contextTranslators.each { it.printProofsToProlog(pto) }

		// ADD THEORIES AND PRAGMA INFORMATION
		pto.closeList()
		pto.printVariable("_Error")
		pto.closeTerm()
	}
}
