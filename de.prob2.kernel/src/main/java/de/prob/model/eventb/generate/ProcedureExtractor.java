package de.prob.model.eventb.generate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.be4.eventbalg.core.parser.node.APostcondition;
import de.be4.eventbalg.core.parser.node.APrecondition;
import de.be4.eventbalg.core.parser.node.AProcedureParseUnit;
import de.be4.eventbalg.core.parser.node.ASimplifiedMachine;
import de.be4.eventbalg.core.parser.node.ATypedIdentifierDefinition;
import de.be4.eventbalg.core.parser.node.ATypingStmt;
import de.be4.eventbalg.core.parser.node.AUntypedIdentifierDefinition;
import de.be4.eventbalg.core.parser.node.PIdentifierDefinition;
import de.be4.eventbalg.core.parser.node.PPostcondition;
import de.be4.eventbalg.core.parser.node.PPrecondition;
import de.be4.eventbalg.core.parser.node.PSimplifiedMachine;
import de.be4.eventbalg.core.parser.node.PTypingStmt;

import de.prob.model.eventb.ModelGenerationException;
import de.prob.model.eventb.algorithm.Procedure;

import org.eventb.core.ast.extension.IFormulaExtension;

public class ProcedureExtractor extends ElementExtractor {

	private final Procedure procedure;

	public ProcedureExtractor(Procedure procedure, AProcedureParseUnit p,
			Set<IFormulaExtension> typeEnv) {
		super(typeEnv);
		Procedure proc = null;
		try {
			Map<String, String> typingInfo = getTypingInformation(p);
			proc = setImplementation(procedure, p.getImplementation());
			proc = addArguments(proc, p.getArguments(), typingInfo);
			proc = addResults(proc, p.getResults(), typingInfo);
			proc = setPrecondition(proc, p.getPrecondition());
			proc = setPostcondition(proc, p.getPostcondition());
			proc = proc.finish();
		} catch (ModelGenerationException e) {
			handleException(e, p);
		}
		this.procedure = proc;
	}

	private Procedure setImplementation(Procedure proc, PSimplifiedMachine impl) {
		if (impl instanceof ASimplifiedMachine) {
			MachineExtractor mE = new MachineExtractor(proc.getConcreteM(),
					typeEnv);
			impl.apply(mE);
			return proc.implementation(mE.getMachineModifier());
		}
		throw new IllegalArgumentException("unknown type");
	}

	private Procedure setPrecondition(Procedure proc, PPrecondition precondition)
			throws ModelGenerationException {
		if (precondition instanceof APrecondition) {
			return proc.precondition(((APrecondition) precondition)
					.getPredicate().getText());
		}
		throw new IllegalArgumentException("Unknown type: "
				+ precondition.getClass());
	}

	private Procedure setPostcondition(Procedure proc,
			PPostcondition postcondition) throws ModelGenerationException {
		if (postcondition instanceof APostcondition) {
			return proc.postcondition(((APostcondition) postcondition)
					.getPredicate().getText());
		}
		throw new IllegalArgumentException("Unknown type: "
				+ postcondition.getClass());
	}

	private Map<String, String> getTypingInformation(
			AProcedureParseUnit parseUnit) {
		Map<String, String> typingInfo = new HashMap<>();
		List<PTypingStmt> typing = parseUnit.getTyping();
		for (PTypingStmt stmt : typing) {
			if (stmt instanceof ATypingStmt) {
				typingInfo.put(((ATypingStmt) stmt).getName().getText(),
						((ATypingStmt) stmt).getExpression().getText());
			}
		}
		return typingInfo;
	}

	private Procedure addArguments(Procedure proc,
			LinkedList<PIdentifierDefinition> args,
			Map<String, String> typingInfo) throws ModelGenerationException {
		Procedure p = proc;
		for (PIdentifierDefinition pID : args) {
			if (pID instanceof ATypedIdentifierDefinition) {
				p = p.argument(((ATypedIdentifierDefinition) pID).getName()
						.getText(), ((ATypedIdentifierDefinition) pID)
						.getType().getText());
			} else if (pID instanceof AUntypedIdentifierDefinition) {
				String name = ((AUntypedIdentifierDefinition) pID).getName()
						.getText();
				if (proc.getArguments().contains(name)) {
					throw new IllegalArgumentException(
							"Typing information already exists for identifier "
									+ proc);
				}
				p = p.argument(name, getType(name, typingInfo));
			}
		}
		return p;
	}

	private Procedure addResults(Procedure proc,
			LinkedList<PIdentifierDefinition> args,
			Map<String, String> typingInfo) throws ModelGenerationException {
		Procedure p = proc;
		for (PIdentifierDefinition pID : args) {
			if (pID instanceof ATypedIdentifierDefinition) {
				p = p.result(((ATypedIdentifierDefinition) pID).getName()
						.getText(), ((ATypedIdentifierDefinition) pID)
						.getType().getText());
			} else if (pID instanceof AUntypedIdentifierDefinition) {
				String name = ((AUntypedIdentifierDefinition) pID).getName()
						.getText();
				if (proc.getResults().contains(name)) {
					throw new IllegalArgumentException(
							"Typing information already exists for identifier "
									+ proc);
				}
				p = p.result(name, getType(name, typingInfo));
			}
		}
		return p;
	}

	private String getType(String idName, Map<String, String> typingInfo) {

		if (!typingInfo.containsKey(idName)) {
			throw new IllegalArgumentException(
					"No typing information found for identifier " + idName);
		}
		return typingInfo.get(idName);
	}

	public Procedure getProcedure() {
		return procedure;
	}

}
