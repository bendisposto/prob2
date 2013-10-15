package de.prob.model.eventb.translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.proof.Tuple;
import de.prob.model.eventb.proof.UncalculatedPO;
import de.prob.model.representation.ModelElementList;

public class ProofExtractor {

	Map<String, String> descriptions;
	Set<String> discharged;

	List<UncalculatedPO> proofs = new ModelElementList<UncalculatedPO>();

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

			File bpoFile = new File(baseFileName + ".bpo");
			ProofDescriptionExtractor ext1 = new ProofDescriptionExtractor();
			saxParser.parse(bpoFile, ext1);

			File bpsFile = new File(baseFileName + ".bps");
			ProofStatusExtractor ext2 = new ProofStatusExtractor();
			saxParser.parse(bpsFile, ext2);

			descriptions = ext1.getProofDescriptions();
			discharged = ext2.getDischargedProofs();
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

			List<Tuple> elements = new ArrayList<Tuple>();
			if ("THM".equals(type)) {
				elements.add(new Tuple("axiom", split[0]));
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
			} else if ("WD".equals(type)) {
				elements.add(new Tuple("axiom", split[0]));
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
			} else {
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
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

			List<Tuple> elements = new ArrayList<Tuple>();
			if ("GRD".equals(type)) {
				Event concreteEvent = m.getEvent(split[0]);
				for (Event event : concreteEvent.getRefines()) {
					if (event.getGuard(split[1]) != null) {
						EventBGuard guard = event.getGuard(split[1]);
						elements.add(new Tuple("event", guard.getParentEvent()
								.getName()));
						elements.add(new Tuple("guard", guard.getName()));
					}
				}
				elements.add(new Tuple("event", concreteEvent.getName()));
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
			} else if ("INV".equals(type)) {
				elements.add(new Tuple("event", "invariant"));
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
			} else if ("THM".equals(type)) {
				if (split.length == 2) {
					elements.add(new Tuple("invariant", split[0]));
				} else {
					elements.add(new Tuple("guard", split[1]));
					elements.add(new Tuple("event", split[0]));
				}
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
			} else if ("WD".equals(type)) {
				if (split.length == 2) {
					elements.add(new Tuple("invariant", split[0]));
				} else {
					Event event = m.getEvent(split[0]);
					if (event.getAction(split[1]) != null) {
						elements.add(new Tuple("event", event.getName()));
						elements.add(new Tuple("action", split[1]));
					} else {
						elements.add(new Tuple("event", event.getName()));
						elements.add(new Tuple("guard", split[1]));
					}
					proofs.add(new UncalculatedPO(source, name, desc, elements,
							isDischarged));
				}
			} else {
				proofs.add(new UncalculatedPO(source, name, desc, elements,
						isDischarged));
			}
		}

	}

	public List<UncalculatedPO> getProofs() {
		return proofs;
	}
}
