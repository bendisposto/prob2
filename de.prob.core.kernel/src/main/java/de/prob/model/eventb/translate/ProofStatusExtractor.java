package de.prob.model.eventb.translate;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProofStatusExtractor extends DefaultHandler {

	private final Set<String> dischargedProofs = new HashSet<String>();

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		if (qName.equals("org.eventb.core.psStatus")) {
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
