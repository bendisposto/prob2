package de.prob.model.eventb.algorithm;

import de.prob.model.eventb.Event
import de.prob.model.eventb.Variant
import de.prob.model.eventb.algorithm.ast.Statement;
import de.prob.model.eventb.algorithm.ast.While;
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.ModelElementList

public class LoopInformation extends AbstractElement {
	Variant variant
	ModelElementList<Event> loopStatements
	Statement stmt
	String stmtName

	public LoopInformation(String stmtName, While stmt, Variant variant, List<Event> loopStatements) {
		this.variant = variant
		this.stmt = stmt
		this.stmtName = stmtName
		this.loopStatements = new ModelElementList<Event>(loopStatements)
	}

	public LoopInformation add(Event loopStmt) {
		new LoopInformation(stmtName, stmt, variant, loopStatements.addElement(loopStmt))
	}
}
