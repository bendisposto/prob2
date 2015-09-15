package de.prob.model.eventb.algorithm;

import de.prob.model.eventb.Event
import de.prob.model.eventb.Variant
import de.prob.model.representation.AbstractElement

public class LoopInformation extends AbstractElement {
	Variant variant
	Event loopBegin
	Event lastStatement
	int startPc
	int lastStmtPc

	public LoopInformation(Variant variant, Event loopBegin, Event lastStatement, int startPc, int lastStmtPc) {
		this.variant = variant
		this.loopBegin = loopBegin
		this.lastStatement = lastStatement
		this.startPc = startPc
		this.lastStmtPc = lastStmtPc
	}
}
