package de.prob.model.eventb.translate

import de.prob.prolog.output.IPrologTermOutput


class PO {
	String source
	String desc
	List<Element> elements
	boolean discharged

	def PO(source, desc, elements, discharged) {
		this.source = source
		this.desc = desc
		this.elements = elements
		this.discharged = discharged
	}

	def toProlog(IPrologTermOutput pto) {
		pto.openTerm("po")
		pto.printAtom(source)
		pto.printAtom(desc)
		pto.openList()
		elements.each {
			pto.openTerm(it.type)
			pto.printAtom(it.value)
			pto.closeTerm()
		}
		pto.closeList()
		pto.printAtom(String.valueOf(discharged))
		pto.closeTerm()
	}
}
