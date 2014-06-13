package de.prob.model.eventb.translate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BSet;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.RefType;
import de.prob.model.representation.RefType.ERefType;

public class ContextXmlHandler extends DefaultHandler {

	private final EventBModel model;
	private final Set<IFormulaExtension> typeEnv;
	private final String directoryPath;
	private final List<String> extendsNames = new ArrayList<String>();

	private final Context context;
	private final ModelElementList<Context> Extends = new ModelElementList<Context>();
	private final ModelElementList<BSet> sets = new ModelElementList<BSet>();
	private final ModelElementList<EventBAxiom> axioms = new ModelElementList<EventBAxiom>();
	private final ModelElementList<EventBAxiom> inheritedAxioms = new ModelElementList<EventBAxiom>();
	private final ModelElementList<EventBConstant> constants = new ModelElementList<EventBConstant>();

	private Context internalContext;
	private ModelElementList<Context> internalExtends;
	private ModelElementList<BSet> internalSets;
	private ModelElementList<EventBAxiom> internalAxioms;
	private ModelElementList<EventBAxiom> internalInheritedAxioms;
	private ModelElementList<EventBConstant> internalConstants;

	private boolean inInternalContext;

	private final Map<String, Map<String, EventBAxiom>> axiomCache = new HashMap<String, Map<String, EventBAxiom>>();

	public ContextXmlHandler(final EventBModel model, final String fileName,
			final boolean isMainComponent, final Set<IFormulaExtension> typeEnv) {
		this.model = model;
		this.typeEnv = typeEnv;

		String name = fileName.substring(
				fileName.lastIndexOf(File.separatorChar) + 1,
				fileName.lastIndexOf("."));
		directoryPath = fileName.substring(0,
				fileName.lastIndexOf(File.separatorChar));
		context = new Context(name, directoryPath);
		model.addContext(context);
		if (isMainComponent) {
			model.setMainComponent(context);
		}

		axiomCache.put(name, new HashMap<String, EventBAxiom>());
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {

		if (qName.equals("org.eventb.core.scInternalContext")) {
			beginInternalContextExtraction(attributes);
		} else if (qName.equals("org.eventb.core.scExtendsContext")) {
			addExtendedContext(attributes);
		} else if (qName.equals("org.eventb.core.scAxiom")) {
			addAxiom(attributes);
		} else if (qName.equals("org.eventb.core.scConstant")) {
			addConstant(attributes);
		} else if (qName.equals("org.eventb.core.scCarrierSet")) {
			addSet(attributes);
		}
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		if (qName.equals("org.eventb.core.scInternalContext")) {
			endInternalContextExtraction();
		}
	}

	private void addSet(final Attributes attributes) {
		String name = attributes.getValue("name");
		BSet bSet = new BSet(name);
		if (inInternalContext) {
			internalSets.add(bSet);
		} else {
			sets.add(bSet);
		}
	}

	private void addConstant(final Attributes attributes) {
		String name = attributes.getValue("name");
		boolean symbolic = "true".equals(attributes
				.getValue("de.prob.symbolic.symbolicAttribute"));
		String unit = attributes.getValue("de.prob.units.unitPragmaAttribute");
		if (inInternalContext) {
			internalConstants.add(new EventBConstant(name, symbolic, unit));
		} else {
			constants.add(new EventBConstant(name, symbolic, unit));
		}
	}

	private void addAxiom(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.source");
		String internalName = source.substring(source.lastIndexOf('#') + 1,
				source.length());
		String filePath = source.substring(0, source.indexOf('|'));
		String contextName = filePath.substring(filePath.lastIndexOf("/") + 1,
				filePath.lastIndexOf("."));

		String label = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		boolean theorem = attributes.getValue("org.eventb.core.theorem")
				.equals("true");

		if (inInternalContext) {
			if (contextName.equals(internalContext.getName())) {
				EventBAxiom axiom = new EventBAxiom(label, predicate, theorem,
						typeEnv);
				internalAxioms.add(axiom);
				axiomCache.get(internalContext.getName()).put(internalName,
						axiom);
			} else {
				internalInheritedAxioms.add(axiomCache.get(contextName).get(
						internalName));
			}
		} else {
			if (contextName.equals(context.getName())) {
				EventBAxiom axiom = new EventBAxiom(label, predicate, theorem,
						typeEnv);
				axioms.add(axiom);
				axiomCache.get(context.getName()).put(internalName, axiom);
			} else {
				inheritedAxioms.add(axiomCache.get(contextName).get(
						internalName));
			}
		}

	}

	private void addExtendedContext(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.scTarget");
		String contextName = source.substring(source.lastIndexOf('#') + 1,
				source.length());

		model.addRelationship(context.getName(), contextName, new RefType(
				ERefType.EXTENDS));

		if (!inInternalContext) {
			extendsNames.add(contextName);
		}

		AbstractElement component = model.getComponent(contextName);
		if (component != null) {
			if (inInternalContext) {
				internalExtends.add((Context) component);
			} else {
				Extends.add((Context) component);
			}
		}
	}

	private void beginInternalContextExtraction(final Attributes attributes) {
		String name = attributes.getValue("name");
		inInternalContext = true;

		internalContext = new Context(name, directoryPath);
		model.addContext(internalContext);
		if (extendsNames.contains(name)) {
			Extends.add(internalContext);
		}
		axiomCache.put(name, new HashMap<String, EventBAxiom>());

		internalExtends = new ModelElementList<Context>();
		internalAxioms = new ModelElementList<EventBAxiom>();
		internalInheritedAxioms = new ModelElementList<EventBAxiom>();
		internalSets = new ModelElementList<BSet>();
		internalConstants = new ModelElementList<EventBConstant>();
	}

	private void endInternalContextExtraction() throws SAXException {
		internalContext.addAxioms(internalAxioms, internalInheritedAxioms);
		internalContext.addConstants(internalConstants);
		internalContext.addExtends(internalExtends);
		internalContext.addSets(internalSets);
		internalContext.addConstants(internalConstants);

		ProofExtractor extractor = new ProofExtractor(internalContext,
				directoryPath + File.separatorChar + internalContext.getName());
		internalContext.addProofs(extractor.getProofs());

		inInternalContext = false;
	}

	@Override
	public void endDocument() throws SAXException {
		context.addAxioms(axioms, inheritedAxioms);
		context.addConstants(constants);
		context.addExtends(Extends);
		context.addSets(sets);
		context.addConstants(constants);

		ProofExtractor extractor = new ProofExtractor(context, directoryPath
				+ File.separatorChar + context.getName());
		context.addProofs(extractor.getProofs());
	}

}
