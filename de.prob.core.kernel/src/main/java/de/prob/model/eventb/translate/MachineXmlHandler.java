package de.prob.model.eventb.translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.eventb.EventParameter;
import de.prob.model.eventb.Variant;
import de.prob.model.eventb.Witness;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BSet;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.RefType;
import de.prob.model.representation.RefType.ERefType;

public class MachineXmlHandler extends DefaultHandler {

	private final EventBModel model;
	private final Set<IFormulaExtension> typeEnv;
	private final EventBMachine machine;
	private final List<String> seesNames = new ArrayList<String>();
	private final String directoryPath;

	private final List<Context> sees = new ModelElementList<Context>();
	private final List<EventBMachine> refines = new ModelElementList<EventBMachine>();
	private final List<EventBInvariant> invariants = new ModelElementList<EventBInvariant>();
	private final List<EventBInvariant> inheritedInvariants = new ArrayList<EventBInvariant>();
	private final List<EventBVariable> variables = new ModelElementList<EventBVariable>();
	private final List<Event> events = new ModelElementList<Event>();
	private final List<Variant> variant = new ModelElementList<Variant>();

	// For extracting internal contexts
	private Context internalContext;
	private List<Context> Extends;
	private List<BSet> sets;
	private List<EventBAxiom> axioms;
	private List<EventBAxiom> inheritedAxioms;
	private List<EventBConstant> constants;
	private boolean extractingContext = false;

	// For extracting events
	private Event event;
	private List<Event> refinesForEvent;
	private List<EventBAction> actions;
	private List<EventBGuard> guards;
	private List<EventParameter> parameters;
	private List<Witness> witnesses;
	private boolean extractingEvent = false;

	private final Map<String, Map<String, EventBAxiom>> axiomCache = new HashMap<String, Map<String, EventBAxiom>>();
	private final Map<String, Map<String, EventBInvariant>> invariantCache = new HashMap<String, Map<String, EventBInvariant>>();
	private final Map<String, Map<String, Event>> eventCache = new HashMap<String, Map<String, Event>>();

	public MachineXmlHandler(final EventBModel model, final String fileName,
			final boolean isMainComponent, final Set<IFormulaExtension> typeEnv) {
		this.model = model;
		this.typeEnv = typeEnv;

		String name = fileName.substring(fileName.lastIndexOf("/") + 1,
				fileName.lastIndexOf("."));
		directoryPath = fileName.substring(0, fileName.lastIndexOf("/"));
		machine = new EventBMachine(name, directoryPath);
		model.addMachine(machine);
		if (isMainComponent) {
			model.setMainComponent(machine);
		}

		axiomCache.put(name, new HashMap<String, EventBAxiom>());
		invariantCache.put(name, new HashMap<String, EventBInvariant>());
		eventCache.put(name, new HashMap<String, Event>());
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {

		if (qName.equals("org.eventb.core.scRefinesMachine")) {
			addRefinedMachine(attributes);
		} else if (qName.equals("org.eventb.core.scSeesContext")) {
			addSeesContext(attributes);
		} else if (qName.equals("org.eventb.core.scInternalContext")) {
			beginContextExtraction(attributes);
		} else if (extractingContext
				&& qName.equals("org.eventb.core.scExtendsContext")) {
			addExtendedContext(attributes);
		} else if (extractingContext && qName.equals("org.eventb.core.scAxiom")) {
			addAxiom(attributes);
		} else if (extractingContext
				&& qName.equals("org.eventb.core.scConstant")) {
			addConstant(attributes);
		} else if (extractingContext
				&& qName.equals("org.eventb.core.scCarrierSet")) {
			addSet(attributes);
		} else if (qName.equals("org.eventb.core.scInvariant")) {
			addInvariant(attributes);
		} else if (qName.equals("org.eventb.core.scVariable")) {
			addVariable(attributes);
		} else if (qName.equals("org.eventb.core.scVariant")) {
			addVariant(attributes);
		} else if (qName.equals("org.eventb.core.scEvent")) {
			beginEventExtraction(attributes);
		} else if (extractingEvent && qName.equals("org.eventb.core.scAction")) {
			addAction(attributes);
		} else if (extractingEvent && qName.equals("org.eventb.core.scGuard")) {
			addGuard(attributes);
		} else if (extractingEvent
				&& qName.equals("org.eventb.core.scParameter")) {
			addEventParameter(attributes);
		} else if (extractingEvent
				&& qName.equals("org.eventb.core.scRefinesEvent")) {
			addRefinedEvent(attributes);
		} else if (extractingEvent && qName.equals("org.eventb.core.scWitness")) {
			addWitness(attributes);
		}

	}

	private void addWitness(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		witnesses.add(new Witness(event, name, predicate, typeEnv));
	}

	private void addRefinedEvent(final Attributes attributes) {
		String target = attributes.getValue("org.eventb.core.scTarget");
		String internalName = target.substring(target.lastIndexOf('#') + 1,
				target.length());
		String fileSource = target.substring(0, target.indexOf('|'));
		String refinedMachineName = fileSource.substring(
				fileSource.lastIndexOf('/') + 1, fileSource.lastIndexOf('.'));
		refinesForEvent.add(eventCache.get(refinedMachineName)
				.get(internalName));
	}

	private void addEventParameter(final Attributes attributes) {
		String name = attributes.getValue("name");
		parameters.add(new EventParameter(event, name));
	}

	private void addGuard(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		boolean theorem = "true".equals(attributes
				.getValue("org.eventb.core.theorem"));
		guards.add(new EventBGuard(event, name, predicate, theorem, typeEnv));
	}

	private void addAction(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String assignment = attributes.getValue("org.eventb.core.assignment");
		actions.add(new EventBAction(event, name, assignment, typeEnv));
	}

	private void beginEventExtraction(final Attributes attributes) {
		String crazyRodinInternalName = attributes.getValue("name");
		String name = attributes.getValue("org.eventb.core.label");
		String convergence = attributes.getValue("org.eventb.core.convergence");
		EventType eventType = "0".equals(convergence) ? EventType.ORDINARY
				: ("1".equals(convergence) ? EventType.CONVERGENT
						: EventType.ANTICIPATED);
		event = new Event(name, eventType);
		events.add(event);
		eventCache.get(machine.getName()).put(crazyRodinInternalName, event);

		extractingEvent = true;

		refinesForEvent = new ModelElementList<Event>();
		guards = new ModelElementList<EventBGuard>();
		actions = new ModelElementList<EventBAction>();
		witnesses = new ModelElementList<Witness>();
		parameters = new ModelElementList<EventParameter>();
	}

	private void addVariant(final Attributes attributes) {
		String expression = attributes.getValue("org.eventb.core.expression");
		variant.add(new Variant(expression, typeEnv));
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {

		if (extractingContext
				&& qName.equals("org.eventb.core.scInternalContext")) {
			endContextExtraction();
		}
		if (extractingEvent && qName.equals("org.eventb.core.scEvent")) {
			endEventExtraction();
		}
	}

	private void endEventExtraction() {
		event.addActions(actions);
		event.addGuards(guards);
		event.addParameters(parameters);
		event.addRefines(refinesForEvent);
		event.addWitness(witnesses);
		extractingEvent = false;
	}

	private void addVariable(final Attributes attributes) {
		String name = attributes.getValue("name");
		variables.add(new EventBVariable(name));
	}

	private void addRefinedMachine(final Attributes attributes) {
		String target = attributes.getValue("org.eventb.core.scTarget");
		String machineName = target.substring(target.lastIndexOf("/") + 1,
				target.lastIndexOf("."));

		model.addRelationship(machine.getName(), machineName, new RefType(
				ERefType.REFINES));

		AbstractElement component = model.getComponent(machineName);
		if (component != null) {
			EventBMachine mch = (EventBMachine) component;
			refines.add(mch);
		} else {
			try {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = parserFactory.newSAXParser();

				String fileName = directoryPath + "/" + machineName + ".bcm";
				MachineXmlHandler handler = new MachineXmlHandler(model,
						fileName, false, typeEnv);
				saxParser.parse(new File(fileName), handler);

				axiomCache.putAll(handler.getAxiomCache());
				invariantCache.putAll(handler.getInvariantCache());
				eventCache.putAll(handler.getEventCache());

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void addInvariant(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.source");
		String internalName = source.substring(source.lastIndexOf('#') + 1,
				source.length());
		String filePath = source.substring(0, source.indexOf("|"));
		String machineName = filePath.substring(filePath.lastIndexOf("/") + 1,
				filePath.lastIndexOf("."));
		if (machineName.equals(machine.getName())) {
			String label = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");
			boolean theorem = attributes.getValue("org.eventb.core.theorem")
					.equals("true");
			EventBInvariant inv = new EventBInvariant(label, predicate,
					theorem, typeEnv);
			invariants.add(inv);
			invariantCache.get(machine.getName()).put(internalName, inv);
		} else {
			inheritedInvariants.add(invariantCache.get(machineName).get(
					internalName));
		}
	}

	private void addSet(final Attributes attributes) {
		String name = attributes.getValue("name");
		sets.add(new BSet(name));
	}

	private void addConstant(final Attributes attributes) {
		String name = attributes.getValue("name");
		boolean symbolic = "true".equals(attributes
				.getValue("de.prob.symbolic.symbolicAttribute"));
		constants.add(new EventBConstant(name, symbolic));
	}

	private void addExtendedContext(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.scTarget");
		String contextName = source.substring(source.lastIndexOf('#') + 1,
				source.length());

		model.addRelationship(internalContext.getName(), contextName,
				new RefType(ERefType.EXTENDS));

		Context extended = (Context) model.getComponent(contextName);
		Extends.add(extended);

	}

	private void addAxiom(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.source");
		String internalName = source.substring(source.lastIndexOf('#') + 1,
				source.length());
		String filePath = source.substring(0, source.indexOf('|'));
		String contextName = filePath.substring(filePath.lastIndexOf("/") + 1,
				filePath.lastIndexOf("."));
		if (contextName.equals(internalContext.getName())) {
			String label = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");
			boolean theorem = attributes.getValue("org.eventb.core.theorem")
					.equals("true");
			EventBAxiom axiom = new EventBAxiom(label, predicate, theorem,
					typeEnv);
			axioms.add(axiom);
			axiomCache.get(internalContext.getName()).put(internalName, axiom);
		} else {
			inheritedAxioms.add(axiomCache.get(contextName).get(internalName));
		}
	}

	private void addSeesContext(final Attributes attributes) {
		String target = attributes.getValue("org.eventb.core.scTarget");
		String contextName = target.substring(target.lastIndexOf("/") + 1,
				target.lastIndexOf("."));

		model.addRelationship(machine.getName(), contextName, new RefType(
				ERefType.SEES));

		seesNames.add(contextName);

		AbstractElement context = model.getComponent(contextName);
		if (context != null) {
			sees.add((Context) context);
		}

	}

	private void beginContextExtraction(final Attributes attributes) {
		String name = attributes.getValue("name");
		if (model.getComponent(name) != null) {
			extractingContext = false;
			return;
		}
		extractingContext = true;

		internalContext = new Context(name, directoryPath);
		model.addContext(internalContext);
		if (seesNames.contains(name)) {
			sees.add(internalContext);
		}
		axiomCache.put(name, new HashMap<String, EventBAxiom>());

		Extends = new ModelElementList<Context>();
		axioms = new ModelElementList<EventBAxiom>();
		inheritedAxioms = new ModelElementList<EventBAxiom>();
		sets = new ModelElementList<BSet>();
		constants = new ModelElementList<EventBConstant>();
	}

	private void endContextExtraction() {
		internalContext.addAxioms(axioms, inheritedAxioms);
		internalContext.addConstants(constants);
		internalContext.addExtends(Extends);
		internalContext.addSets(sets);
		internalContext.addConstants(constants);
		extractingContext = false;
	}

	@Override
	public void endDocument() throws SAXException {
		machine.addEvents(events);
		machine.addInvariants(invariants, inheritedInvariants);
		machine.addRefines(refines);
		machine.addSees(sees);
		machine.addVariables(variables);
		machine.addVariant(variant);

		ProofExtractor proofExtractor = new ProofExtractor(machine,
				directoryPath + "/" + machine.getName());
		machine.addProofs(proofExtractor.getProofs());
	}

	public Map<String, Map<String, EventBAxiom>> getAxiomCache() {
		return axiomCache;
	}

	public Map<String, Map<String, EventBInvariant>> getInvariantCache() {
		return invariantCache;
	}

	public Map<String, Map<String, Event>> getEventCache() {
		return eventCache;
	}
}
