package de.prob.model.eventb.translate;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProofDescriptionExtractor extends DefaultHandler {

	private final Map<String, String> proofDescriptions = new HashMap<String, String>();

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		if (qName.equals("org.eventb.core.poSequent")) {
			String name = attributes.getValue("name");
			String desc = attributes.getValue("org.eventb.core.poDesc");
			proofDescriptions.put(name, desc);
		}
	}

	public Map<String, String> getProofDescriptions() {
		return proofDescriptions;
	}

}
