package de.prob.model.eventb.translate;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob.model.eventb.EventBModel;

public class ContextXmlHandler extends DefaultHandler {

	private final EventBModel model;
	private final boolean isMainComponent;

	public ContextXmlHandler(final EventBModel model,
			final boolean isMainComponent) {
		this.model = model;
		this.isMainComponent = isMainComponent;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);

	}

}
