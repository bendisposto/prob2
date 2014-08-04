package de.prob.model.eventb.translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.parboiled.common.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.ProofObligation;
import de.prob.model.representation.ModelElementList;

public class ProofExtractor {

	Logger logger = LoggerFactory.getLogger(ProofExtractor.class);

	Map<String, String> descriptions;
	Set<String> discharged;

	ModelElementList<ProofObligation> proofs = new ModelElementList<ProofObligation>();

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
			SAXParser saxParser = parserFactory.newSAXParser();

			String bpoFileName = baseFileName + ".bpo";
			File bpoFile = new File(bpoFileName);
			if (bpoFile.exists()) {
				ProofDescriptionExtractor ext1 = new ProofDescriptionExtractor();
				saxParser.parse(bpoFile, ext1);
				descriptions = ext1.getProofDescriptions();
			} else {
				descriptions = new HashMap<String, String>();
				logger.info("Could not find file "
						+ bpoFileName
						+ ". Assuming that no proofs have been generated for model element.");
			}

			String bpsFileName = baseFileName + ".bps";
			File bpsFile = new File(bpsFileName);
			if (bpsFile.exists()) {
				ProofStatusExtractor ext2 = new ProofStatusExtractor();
				saxParser.parse(bpsFile, ext2);
				discharged = ext2.getDischargedProofs();
			} else {
				discharged = new HashSet<String>();
				logger.info("Could not find file "
						+ bpsFileName
						+ ". Assuming that no proofs are discharged for model element.");
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addProofs(final Context c) {
		for (Entry<String, String> entry : descriptions.entrySet()) {
			String name = entry.getKey();
			String desc = entry.getValue();

			boolean isDischarged = discharged.contains(name);

			String[] split = name.split("/");
			String type = split.length == 1 ? split[0]
					: (split.length == 2 ? split[1] : split[2]);
			String source = c.getName();

			List<Tuple2<String, String>> elements = new ArrayList<Tuple2<String, String>>();
			if ("THM".equals(type)) {
				elements.add(new Tuple2<String, String>("axiom", split[0]));
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			} else if ("WD".equals(type)) {
				elements.add(new Tuple2<String, String>("axiom", split[0]));
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			} else {
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			}
		}
	}

	private void addProofs(final EventBMachine m) {
		for (Entry<String, String> entry : descriptions.entrySet()) {
			String name = entry.getKey();
			String desc = entry.getValue();

			boolean isDischarged = discharged.contains(name);

			String[] split = name.split("/");
			String type = split.length == 1 ? split[0]
					: (split.length == 2 ? split[1] : split[2]);
			String source = m.getName();

			List<Tuple2<String, String>> elements = new ArrayList<Tuple2<String, String>>();
			if ("GRD".equals(type)) {
				Event concreteEvent = m.getEvent(split[0]);
				for (Event event : concreteEvent.getRefines()) {
					if (event.getGuard(split[1]) != null) {
						EventBGuard guard = event.getGuard(split[1]);
						elements.add(new Tuple2<String, String>("event", guard
								.getParentEvent().getName()));
						elements.add(new Tuple2<String, String>("guard", guard
								.getName()));
					}
				}
				elements.add(new Tuple2<String, String>("event", concreteEvent
						.getName()));
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			} else if ("INV".equals(type)) {
				elements.add(new Tuple2<String, String>("event", "invariant"));
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			} else if ("THM".equals(type)) {
				if (split.length == 2) {
					elements.add(new Tuple2<String, String>("invariant",
							split[0]));
				} else {
					elements.add(new Tuple2<String, String>("guard", split[1]));
					elements.add(new Tuple2<String, String>("event", split[0]));
				}
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			} else if ("WD".equals(type)) {
				if (split.length == 2) {
					elements.add(new Tuple2<String, String>("invariant",
							split[0]));
				} else {
					Event event = m.getEvent(split[0]);
					if (event.getAction(split[1]) != null) {
						elements.add(new Tuple2<String, String>("event", event
								.getName()));
						elements.add(new Tuple2<String, String>("action",
								split[1]));
					} else {
						elements.add(new Tuple2<String, String>("event", event
								.getName()));
						elements.add(new Tuple2<String, String>("guard",
								split[1]));
					}
					proofs.add(new ProofObligation(source, name, isDischarged,
							desc, elements));
				}
			} else {
				proofs.add(new ProofObligation(source, name, isDischarged,
						desc, elements));
			}
		}

	}

	public ModelElementList<ProofObligation> getProofs() {
		return proofs;
	}
}
