package de.prob.model.eventb.translate;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ProofStatusExtractor extends DefaultHandler {

	private final Set<String> dischargedProofs = new HashSet<>();

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes) {
		if ("org.eventb.core.psStatus".equals(qName)) {
			String name = attributes.getValue("name");
			boolean discharged = "1000".equals(attributes
					.getValue("org.eventb.core.confidence"));
			if (discharged) {
				dischargedProofs.add(name);
			}
		}
	}

	public Set<String> getDischargedProofs() {
		return dischargedProofs;
	}

}
