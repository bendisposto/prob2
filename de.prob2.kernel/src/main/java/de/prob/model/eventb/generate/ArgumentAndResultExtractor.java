package de.prob.model.eventb.generate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.be4.eventbalg.core.parser.node.AProcedureParseUnit;
import de.be4.eventbalg.core.parser.node.ATypedIdentifierDefinition;
import de.be4.eventbalg.core.parser.node.ATypingStmt;
import de.be4.eventbalg.core.parser.node.AUntypedIdentifierDefinition;
import de.be4.eventbalg.core.parser.node.PIdentifierDefinition;
import de.be4.eventbalg.core.parser.node.PTypingStmt;

public class ArgumentAndResultExtractor {
	private Map<String, String> args;
	private Map<String, String> results;

	public ArgumentAndResultExtractor(AProcedureParseUnit parseUnit) {
		Map<String, String> typingInformation = getTypingInformation(parseUnit);
		args = getIdentifiers(parseUnit.getArguments(), typingInformation);
		results = getIdentifiers(parseUnit.getResults(), typingInformation);

		Set<String> allIds = new HashSet<>();
		allIds.addAll(args.keySet());
		allIds.addAll(results.keySet());
		if (allIds.size() != (args.size() + results.size())) {
			throw new IllegalArgumentException(
					"Illegal procedure definition. Arguments and results may not have the same name");
		}
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

	private Map<String, String> getIdentifiers(
			LinkedList<PIdentifierDefinition> ids,
			Map<String, String> typingInfo) {
		Map<String, String> idList = new HashMap<>();
		for (PIdentifierDefinition pID : ids) {
			if (pID instanceof ATypedIdentifierDefinition) {
				idList.put(((ATypedIdentifierDefinition) pID).getName()
						.getText(), ((ATypedIdentifierDefinition) pID)
						.getType().getText());
			} else if (pID instanceof AUntypedIdentifierDefinition) {
				String text = ((AUntypedIdentifierDefinition) pID).getName()
						.getText();
				if (!typingInfo.containsKey(text)) {
					throw new IllegalArgumentException(
							"No typing information found for identifier "
									+ text);
				}
				if (idList.containsKey(text)) {
					throw new IllegalArgumentException(
							"Typing information already exists for identifier "
									+ text);
				}
				idList.put(text, typingInfo.get(text));
			} else {
				throw new IllegalArgumentException("Unknown identifier type");
			}
		}
		return idList;
	}

	public Map<String, String> getArguments() {
		return args;
	}

	public Map<String, String> getResults() {
		return results;
	}
}
