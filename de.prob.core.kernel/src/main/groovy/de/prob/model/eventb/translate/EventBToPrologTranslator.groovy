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
	List<PO> proofInformation

	def EventBToPrologTranslator(EventBModel model, List<PO> proofInformation) {
		this.model = model
		this.proofInformation = proofInformation
		contextTranslators = model.getChildrenOfType(Context.class).collect { new ContextToAst(it) }
		machineTranslators = model.getChildrenOfType(Machine.class).collect { new MachineToAst(it) }
	}

	def printProlog(ASTProlog astPrinter, IPrologTermOutput pto) {
		def timeTotal = System.currentTimeMillis()

		def time = System.currentTimeMillis()
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
		println "Print machines and contexts: "+(System.currentTimeMillis() - time)

		time = System.currentTimeMillis()
		proofInformation.each { it.toProlog(pto) }
		println "Print proofs: "+(System.currentTimeMillis() - time)

		// ADD THEORIES AND PRAGMA INFORMATION

		pto.closeList()
		pto.printVariable("_Error")
		pto.closeTerm()
		println "Total printing: "+(System.currentTimeMillis() - timeTotal)
	}
}
