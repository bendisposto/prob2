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

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.ProofObligation;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.Constant;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.ModelElementList;

public class ContextXmlHandler extends DefaultHandler {

	private EventBModel model;
	private final Set<IFormulaExtension> typeEnv;
	private final String directoryPath;
	private final List<String> extendsNames = new ArrayList<String>();

	private Context context;
	private final List<Context> Extends = new ArrayList<Context>();
	private final List<de.prob.model.representation.Set> sets = new ArrayList<de.prob.model.representation.Set>();
	private final List<EventBAxiom> axioms = new ArrayList<EventBAxiom>();
	private final List<EventBAxiom> inheritedAxioms = new ArrayList<EventBAxiom>();
	private final List<EventBConstant> constants = new ArrayList<EventBConstant>();

	private Context internalContext;
	private List<Context> internalExtends;
	private List<de.prob.model.representation.Set> internalSets;
	private List<EventBAxiom> internalAxioms;
	private List<EventBAxiom> internalInheritedAxioms;
	private List<EventBConstant> internalConstants;

	private boolean inInternalContext;

	private final Map<String, Map<String, EventBAxiom>> axiomCache = new HashMap<String, Map<String, EventBAxiom>>();

	public ContextXmlHandler(final EventBModel model, final String fileName, final Set<IFormulaExtension> typeEnv) {
		this.model = model;
		this.typeEnv = typeEnv;

		String name = fileName.substring(
				fileName.lastIndexOf(File.separatorChar) + 1,
				fileName.lastIndexOf("."));
		directoryPath = fileName.substring(0,
				fileName.lastIndexOf(File.separatorChar));
		context = new Context(name);

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
		de.prob.model.representation.Set bSet = new de.prob.model.representation.Set(
				new EventB(name));
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

		model.addRelationship(context.getName(), contextName, ERefType.EXTENDS);

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

		internalContext = new Context(name);
		axiomCache.put(name, new HashMap<String, EventBAxiom>());

		internalExtends = new ArrayList<Context>();
		internalAxioms = new ArrayList<EventBAxiom>();
		internalInheritedAxioms = new ArrayList<EventBAxiom>();
		internalSets = new ArrayList<de.prob.model.representation.Set>();
		internalConstants = new ArrayList<EventBConstant>();
	}

	private void endInternalContextExtraction() throws SAXException {
		ModelElementList<EventBAxiom> axms = new ModelElementList<EventBAxiom>(internalInheritedAxioms);
		axms.addMultiple(internalAxioms);
		internalContext = internalContext.set(Axiom.class, axms);
		internalContext = internalContext.set(Constant.class, new ModelElementList<EventBConstant>(internalConstants));
		internalContext = internalContext.set(Context.class, new ModelElementList<Context>(internalExtends));
		internalContext = internalContext.set(de.prob.model.representation.Set.class, new ModelElementList<de.prob.model.representation.Set>(internalSets));

		ProofExtractor extractor = new ProofExtractor(internalContext,
				directoryPath + File.separatorChar + internalContext.getName());
		internalContext = internalContext.set(ProofObligation.class, extractor.getProofs());
		if (extendsNames.contains(internalContext.getName())) {
			Extends.add(internalContext);
		}

		model = model.addContext(internalContext);
		inInternalContext = false;
	}

	@Override
	public void endDocument() throws SAXException {
		ModelElementList<EventBAxiom> axms = new ModelElementList<EventBAxiom>(inheritedAxioms);
		axms = axms.addMultiple(axioms);
		context = context.set(Axiom.class, axms);
		context = context.set(Constant.class, new ModelElementList<EventBConstant>(constants));
		context = context.set(Context.class, new ModelElementList<Context>(Extends));
		context = context.set(de.prob.model.representation.Set.class, new ModelElementList<de.prob.model.representation.Set>(sets));

		ProofExtractor extractor = new ProofExtractor(context, directoryPath
				+ File.separatorChar + context.getName());
		context = context.set(ProofObligation.class, extractor.getProofs());

		model = model.addContext(context);
	}

	public Context getContext() {
		return context;
	}

	public EventBModel getModel() {
		return model;
	}

}
