package de.prob.model.eventb.translate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.theory.AxiomaticDefinitionsBlock;
import de.prob.model.eventb.theory.DataType;
import de.prob.model.eventb.theory.Operator;
import de.prob.model.eventb.theory.ProofRulesBlock;
import de.prob.model.eventb.theory.Theorem;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.eventb.theory.Type;
import de.prob.model.representation.ModelElementList;

public class TheoryXmlHandler extends DefaultHandler {

	private final String workspacePath;
	private final Set<IFormulaExtension> typeEnv = new HashSet<IFormulaExtension>();
	private final EventBModel model;
	private final List<Theory> theories = new ModelElementList<Theory>();
	private final HashMap<String, Theory> theoryMap = new HashMap<String, Theory>();

	public TheoryXmlHandler(final EventBModel model, final String workspacePath) {
		this.model = model;
		this.workspacePath = workspacePath;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		if (qName.equals("org.eventb.theory.core.availableTheory")) {
			String path = attributes
					.getValue("org.eventb.theory.core.availableTheory");
			path = path.substring(0, path.indexOf('|'));
			String name = path.substring(path.lastIndexOf('/') + 1,
					path.lastIndexOf('.'));

			try {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = parserFactory.newSAXParser();

				TheoryExtractor extractor = new TheoryExtractor(name);
				saxParser.parse(new File(workspacePath + path), extractor);
				theories.add(extractor.getTheory());
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Set<IFormulaExtension> getTypeEnv() {
		return typeEnv;
	}

	@Override
	public void endDocument() throws SAXException {
		model.addTheories(theories);
	}

	class TheoryExtractor extends DefaultHandler {

		private final Theory theory;
		private final List<Theory> imported = new ModelElementList<Theory>();
		private final List<Type> typeParameters = new ModelElementList<Type>();
		private final List<DataType> dataTypes = new ModelElementList<DataType>();
		private final List<Operator> operators = new ModelElementList<Operator>();
		private final List<AxiomaticDefinitionsBlock> axiomaticDefBlocks = new ModelElementList<AxiomaticDefinitionsBlock>();
		private final List<Theorem> theorems = new ModelElementList<Theorem>();
		private final List<ProofRulesBlock> proofRules = new ModelElementList<ProofRulesBlock>();

		public TheoryExtractor(final String name) {
			theory = new Theory(name);
		}

		public Theory getTheory() {
			return theory;
		}

		@Override
		public void startElement(final String uri, final String localName,
				final String qName, final Attributes attributes)
				throws SAXException {
			if (qName.equals("org.eventb.theory.core.scTypeParameter")) {
				addTypeParameter(attributes);
			} else if (qName.equals("")) {

			}
		}

		private void addTypeParameter(final Attributes attributes) {
			// TODO Auto-generated method stub

		}

		@Override
		public void endDocument() throws SAXException {
			theory.addAxiomaticDefinitions(axiomaticDefBlocks);
			theory.addDataTypes(dataTypes);
			theory.addImported(imported);
			theory.addOperators(operators);
			theory.addProofRules(proofRules);
			theory.addTheorems(theorems);
			theory.addTypeParameters(typeParameters);
		}
	}

}
