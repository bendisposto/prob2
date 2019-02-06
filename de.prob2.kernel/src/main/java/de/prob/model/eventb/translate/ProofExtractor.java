package de.prob.model.eventb.translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.ProofObligation;
import de.prob.model.representation.ModelElementList;
import de.prob.util.Tuple2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.SAXException;

public class ProofExtractor {
	private static final Logger logger = LoggerFactory.getLogger(ProofExtractor.class);

	private Map<String, String> descriptions;
	private Set<String> discharged;

	private final List<ProofObligation> proofs = new ArrayList<>();

	public ProofExtractor(final Context c, final String baseFileName)
			throws SAXException {
		extractProofs(baseFileName);
		addProofs(c);
	}

	public ProofExtractor(final EventBMachine m, final String baseFileName)
			throws SAXException {
		extractProofs(baseFileName);
		addProofs(m);
	}

	private void extractProofs(final String baseFileName) throws SAXException {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			SAXParser saxParser = parserFactory.newSAXParser();

			String bpoFileName = baseFileName + ".bpo";
			File bpoFile = new File(bpoFileName);
			if (bpoFile.exists()) {
				ProofDescriptionExtractor ext1 = new ProofDescriptionExtractor();
				saxParser.parse(bpoFile, ext1);
				descriptions = ext1.getProofDescriptions();
			} else {
				descriptions = new HashMap<>();
				logger.info("Could not find file {}. Assuming that no proofs have been generated for model element.", bpoFileName);
			}

			String bpsFileName = baseFileName + ".bps";
			File bpsFile = new File(bpsFileName);
			if (bpsFile.exists()) {
				ProofStatusExtractor ext2 = new ProofStatusExtractor();
				saxParser.parse(bpsFile, ext2);
				discharged = ext2.getDischargedProofs();
			} else {
				discharged = new HashSet<>();
				logger.info("Could not find file {}. Assuming that no proofs are discharged for model element.", bpsFileName);
			}
		} catch (ParserConfigurationException | IOException e) {
			logger.error("Error extracting proof", e);
		}
	}

	private void addProofs(final Context c) {
		for (Map.Entry<String, String> entry : descriptions.entrySet()) {
			String name = entry.getKey();
			String desc = entry.getValue();

			boolean isDischarged = discharged.contains(name);

			String[] split = name.split("/");
			String type;
			if (split.length == 1) {
				type = split[0];
			} else if (split.length == 2) {
				type = split[1];
			} else {
				type = split[2];
			}
			String source = c.getName();

			List<Tuple2<String, String>> elements = new ArrayList<>();
			if ("THM".equals(type) || "WD".equals(type)) {
				elements.add(new Tuple2<>("axiom", split[0]));
			}
			proofs.add(new ProofObligation(source, name, isDischarged, desc, elements));
		}
	}

	private void addProofs(final EventBMachine m) {
		for (Map.Entry<String, String> entry : descriptions.entrySet()) {
			String name = entry.getKey();
			String desc = entry.getValue();

			boolean isDischarged = discharged.contains(name);

			String[] split = name.split("/");
			String type;
			if (split.length == 1) {
				type = split[0];
			} else if (split.length == 2) {
				type = split[1];
			} else {
				type = split[2];
			}
			String source = m.getName();

			List<Tuple2<String, String>> elements = new ArrayList<>();
			if ("GRD".equals(type)) {
				Event concreteEvent = m.getEvent(split[0]);
				for (Event event : concreteEvent.getRefines()) {
					if (event.getGuards().getElement(split[1]) != null) {
						EventBGuard guard = event.getGuards().getElement(split[1]);
						elements.add(new Tuple2<>("event", event.getName()));
						elements.add(new Tuple2<>("guard", guard.getName()));
					}
				}
				elements.add(new Tuple2<>("event", concreteEvent.getName()));
				proofs.add(new ProofObligation(source, name, isDischarged, desc, elements));
			} else if ("INV".equals(type)) {
				elements.add(new Tuple2<>("event", "invariant"));
				proofs.add(new ProofObligation(source, name, isDischarged, desc, elements));
			} else if ("THM".equals(type)) {
				if (split.length == 2) {
					elements.add(new Tuple2<>("invariant", split[0]));
				} else {
					elements.add(new Tuple2<>("guard", split[1]));
					elements.add(new Tuple2<>("event", split[0]));
				}
				proofs.add(new ProofObligation(source, name, isDischarged, desc, elements));
			} else if ("WD".equals(type)) {
				if (split.length == 2) {
					elements.add(new Tuple2<>("invariant", split[0]));
				} else {
					Event event = m.getEvent(split[0]);
					if (event.getActions().getElement(split[1]) != null) {
						elements.add(new Tuple2<>("event", event.getName()));
						elements.add(new Tuple2<>("action", split[1]));
					} else {
						elements.add(new Tuple2<>("event", event.getName()));
						elements.add(new Tuple2<>("guard", split[1]));
					}
					proofs.add(new ProofObligation(source, name, isDischarged, desc, elements));
				}
			} else {
				proofs.add(new ProofObligation(source, name, isDischarged, desc, elements));
			}
		}

	}

	public ModelElementList<ProofObligation> getProofs() {
		return new ModelElementList<>(proofs);
	}
}
