package de.prob.model.eventb.generate;

import java.util.ArrayList;
import java.util.List;

import de.be4.eventbalg.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventbalg.core.parser.node.AContextParseUnit;
import de.be4.eventbalg.core.parser.node.AMachineParseUnit;
import de.be4.eventbalg.core.parser.node.AProcedureParseUnit;
import de.be4.eventbalg.core.parser.node.TIdentifierLiteral;

public class ReferenceExtractor extends DepthFirstAdapter {

	private boolean machine = false;
	private boolean context = false;
	private List<String> refines = new ArrayList<>();
	private List<String> sees = new ArrayList<>();
	private List<String> extendsL = new ArrayList<>();

	public boolean isMachine() {
		return machine;
	}

	public boolean isContext() {
		return context;
	}

	public List<String> getRefines() {
		return refines;
	}

	public List<String> getSees() {
		return sees;
	}

	public List<String> getExtends() {
		return extendsL;
	}

	@Override
	public void caseAMachineParseUnit(AMachineParseUnit node) {
		machine = true;
		for (TIdentifierLiteral contextName : node.getSeenNames()) {
			sees.add(contextName.getText());
		}
		for (TIdentifierLiteral mchName : node.getRefinesNames()) {
			refines.add(mchName.getText());
		}
	}

	@Override
	public void caseAContextParseUnit(AContextParseUnit node) {
		context = true;
		for (TIdentifierLiteral ctx : node.getExtendsNames()) {
			extendsL.add(ctx.getText());
		}
	}

	@Override
	public void caseAProcedureParseUnit(AProcedureParseUnit node) {
		machine = true;
		for (TIdentifierLiteral ctx : node.getSeen()) {
			sees.add(ctx.getText());
		}
	}

}
