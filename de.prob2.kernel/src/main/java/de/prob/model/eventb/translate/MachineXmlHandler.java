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

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.eventb.EventParameter;
import de.prob.model.eventb.ProofObligation;
import de.prob.model.eventb.Variant;
import de.prob.model.eventb.Witness;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Action;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Constant;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Variable;

import org.eventb.core.ast.extension.IFormulaExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MachineXmlHandler extends DefaultHandler {
	private static final Logger logger = LoggerFactory.getLogger(MachineXmlHandler.class);

	private EventBModel model;
	private final Set<IFormulaExtension> typeEnv;
	private EventBMachine machine;
	private final List<String> seesNames = new ArrayList<>();
	private final String directoryPath;

	private final List<Context> sees = new ArrayList<>();
	private final List<EventBMachine> refines = new ArrayList<>();
	private final List<EventBInvariant> invariants = new ArrayList<>();
	private final List<EventBInvariant> inheritedInvariants = new ArrayList<>();
	private final List<EventBVariable> variables = new ArrayList<>();
	private final List<Event> events = new ArrayList<>();
	private final List<Variant> variant = new ArrayList<>();

	// For extracting internal contexts
	private Context internalContext;
	private List<Context> extendedContexts;
	private List<de.prob.model.representation.Set> sets;
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

	private final Map<String, Map<String, EventBAxiom>> axiomCache = new HashMap<>();
	private final Map<String, Map<String, EventBInvariant>> invariantCache = new HashMap<>();
	private final Map<String, Map<String, Event>> eventCache = new HashMap<>();

	public MachineXmlHandler(EventBModel model, final String fileName, final Set<IFormulaExtension> typeEnv) {
		this.model = model;
		this.typeEnv = typeEnv;

		String name = fileName.substring(
				fileName.lastIndexOf(File.separatorChar) + 1,
				fileName.lastIndexOf('.'));
		directoryPath = fileName.substring(0,
				fileName.lastIndexOf(File.separatorChar));
		machine = new EventBMachine(name);

		axiomCache.put(name, new HashMap<>());
		invariantCache.put(name, new HashMap<>());
		eventCache.put(name, new HashMap<>());
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes) {
		switch (qName) {
			case "org.eventb.core.scRefinesMachine":
				addRefinedMachine(attributes);
				break;
			case "org.eventb.core.scSeesContext":
				addSeesContext(attributes);
				break;
			case "org.eventb.core.scInternalContext":
				beginContextExtraction(attributes);
				break;
			case "org.eventb.core.scExtendsContext":
				if (extractingContext) {
					addExtendedContext(attributes);
				}
				break;
			case "org.eventb.core.scAxiom":
				if (extractingContext) {
					addAxiom(attributes);
				}
				break;
			case "org.eventb.core.scConstant":
				if (extractingContext) {
					addConstant(attributes);
				}
				break;
			case "org.eventb.core.scCarrierSet":
				if (extractingContext) {
					addSet(attributes);
				}
				break;
			case "org.eventb.core.scInvariant":
				addInvariant(attributes);
				break;
			case "org.eventb.core.scVariable":
				addVariable(attributes);
				break;
			case "org.eventb.core.scVariant":
				addVariant(attributes);
				break;
			case "org.eventb.core.scEvent":
				beginEventExtraction(attributes);
				break;
			case "org.eventb.core.scAction":
				if (extractingEvent) {
					addAction(attributes);
				}
				break;
			case "org.eventb.core.scGuard":
				if (extractingEvent) {
					addGuard(attributes);
				}
				break;
			case "org.eventb.core.scParameter":
				if (extractingEvent) {
					addEventParameter(attributes);
				}
				break;
			case "org.eventb.core.scRefinesEvent":
				if (extractingEvent) {
					addRefinedEvent(attributes);
				}
				break;
			case "org.eventb.core.scWitness":
				if (extractingEvent) {
					addWitness(attributes);
				}
				break;
		}
	}

	private void addWitness(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		witnesses.add(new Witness(name, predicate, typeEnv));
	}

	private void addRefinedEvent(final Attributes attributes) {
		String target = attributes.getValue("org.eventb.core.scTarget");
		String internalName = target.substring(target.lastIndexOf('#') + 1,
				target.length());

		if (internalName.endsWith("\\\\")) {
			internalName = internalName.substring(0, internalName.length() - 1);
		} else {
			internalName = internalName.replace("\\", "");
		}

		String fileSource = target.substring(0, target.indexOf('|'));

		String refinedMachineName = fileSource.substring(
				fileSource.lastIndexOf('/') + 1, fileSource.lastIndexOf('.'));
		refinesForEvent.add(eventCache.get(refinedMachineName)
				.get(internalName));
	}

	private void addEventParameter(final Attributes attributes) {
		String name = attributes.getValue("name");
		parameters.add(new EventParameter(name));
	}

	private void addGuard(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		boolean theorem = "true".equals(attributes
				.getValue("org.eventb.core.theorem"));
		guards.add(new EventBGuard(name, predicate, theorem, typeEnv));
	}

	private void addAction(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String assignment = attributes.getValue("org.eventb.core.assignment");
		actions.add(new EventBAction(name, assignment, typeEnv));
	}

	private void beginEventExtraction(final Attributes attributes) {
		String crazyRodinInternalName = attributes.getValue("name");
		String name = attributes.getValue("org.eventb.core.label");
		String convergence = attributes.getValue("org.eventb.core.convergence");
		String extended = attributes.getValue("org.eventb.core.extended");
		Event.EventType eventType;
		if ("0".equals(convergence)) {
			eventType = Event.EventType.ORDINARY;
		} else if ("1".equals(convergence)) {
			eventType = Event.EventType.CONVERGENT;
		} else {
			eventType = Event.EventType.ANTICIPATED;
		}
		event = new Event(name, eventType, Boolean.valueOf(extended));
		eventCache.get(machine.getName()).put(crazyRodinInternalName, event);

		extractingEvent = true;

		refinesForEvent = new ArrayList<>();
		guards = new ArrayList<>();
		actions = new ArrayList<>();
		witnesses = new ArrayList<>();
		parameters = new ArrayList<>();
	}

	private void addVariant(final Attributes attributes) {
		String expression = attributes.getValue("org.eventb.core.expression");
		variant.add(new Variant(expression, typeEnv));
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {

		if (extractingContext
				&& "org.eventb.core.scInternalContext".equals(qName)) {
			endContextExtraction();
		}
		if (extractingEvent && "org.eventb.core.scEvent".equals(qName)) {
			endEventExtraction();
		}
	}

	private void endEventExtraction() {
		event = event.set(Action.class, new ModelElementList<>(actions));
		event = event.set(Guard.class, new ModelElementList<>(guards));
		event = event.set(EventParameter.class, new ModelElementList<>(parameters));
		event = event.set(Event.class, new ModelElementList<>(refinesForEvent));
		event = event.set(Witness.class, new ModelElementList<>(witnesses));

		events.add(event);
		extractingEvent = false;
	}

	private void addVariable(final Attributes attributes) {
		String name = attributes.getValue("name");
		boolean concrete = "true".equals(attributes
				.getValue("org.eventb.core.concrete"));
		String unit = attributes.getValue("de.prob.units.unitPragmaAttribute");
		if (concrete) {
			variables.add(new EventBVariable(name, unit));
		}
	}

	private void addRefinedMachine(final Attributes attributes) {
		String target = attributes.getValue("org.eventb.core.scTarget");
		String machineName = target.substring(target.lastIndexOf('/') + 1,
				target.lastIndexOf('.'));

		model = model.addRelationship(machine.getName(), machineName, ERefType.REFINES);

		AbstractElement component = model.getComponent(machineName);
		if (component != null) {
			EventBMachine mch = (EventBMachine) component;
			refines.add(mch);
		} else {
			try {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = parserFactory.newSAXParser();

				String fileName = directoryPath + File.separatorChar
						+ machineName + ".bcm";
				MachineXmlHandler handler = new MachineXmlHandler(model,
						fileName, typeEnv);
				saxParser.parse(new File(fileName), handler);

				axiomCache.putAll(handler.getAxiomCache());
				invariantCache.putAll(handler.getInvariantCache());
				eventCache.putAll(handler.getEventCache());

				refines.add(handler.getMachine());

				model = handler.getModel();
			} catch (IOException | ParserConfigurationException | SAXException e) {
				logger.error("Error parsing XML",e);
			}
		}
	}

	private void addInvariant(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.source");
		String internalName = source.substring(source.lastIndexOf('#') + 1,
				source.length());
		internalName = internalName.replace("\\", "");
		String filePath = source.substring(0, source.indexOf('|'));
		String machineName = filePath.substring(filePath.lastIndexOf('/') + 1,
				filePath.lastIndexOf('.'));
		if (machineName.equals(machine.getName())) {
			String label = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");
			boolean theorem = "true".equals(attributes.getValue("org.eventb.core.theorem"));
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
		sets.add(new de.prob.model.representation.Set(new EventB(name)));
	}

	private void addConstant(final Attributes attributes) {
		String name = attributes.getValue("name");
		boolean symbolic = "true".equals(attributes
				.getValue("de.prob.symbolic.symbolicAttribute"));
		String unit = attributes.getValue("de.prob.units.unitPragmaAttribute");
		constants.add(new EventBConstant(name, symbolic, unit));
	}

	private void addExtendedContext(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.scTarget");
		String contextName = source.substring(source.lastIndexOf('#') + 1,
				source.length());

		model.addRelationship(internalContext.getName(), contextName,
				ERefType.EXTENDS);

		Context extended = (Context) model.getComponent(contextName);
		extendedContexts.add(extended);

	}

	private void addAxiom(final Attributes attributes) {
		String source = attributes.getValue("org.eventb.core.source");
		String internalName = source.substring(source.lastIndexOf('#') + 1,
				source.length());
		internalName = internalName.replace("\\", "");
		String filePath = source.substring(0, source.indexOf('|'));
		String contextName = filePath.substring(filePath.lastIndexOf('/') + 1,
				filePath.lastIndexOf('.'));
		if (contextName.equals(internalContext.getName())) {
			String label = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");
			boolean theorem = "true".equals(attributes.getValue("org.eventb.core.theorem"));
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
		String contextName = target.substring(target.lastIndexOf('/') + 1,
				target.lastIndexOf('.'));

		model = model.addRelationship(machine.getName(), contextName, ERefType.SEES);

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

		internalContext = new Context(name);

		axiomCache.put(name, new HashMap<>());

		extendedContexts = new ArrayList<>();
		axioms = new ArrayList<>();
		inheritedAxioms = new ArrayList<>();
		sets = new ArrayList<>();
		constants = new ArrayList<>();
	}

	private void endContextExtraction() throws SAXException {
		ModelElementList<EventBAxiom> axms = new ModelElementList<>(inheritedAxioms);
		axms = axms.addMultiple(axioms);
		internalContext = internalContext.set(Axiom.class, axms);
		internalContext = internalContext.set(Constant.class, new ModelElementList<>(constants));
		internalContext = internalContext.set(Context.class, new ModelElementList<>(extendedContexts));
		internalContext = internalContext.set(de.prob.model.representation.Set.class,
			new ModelElementList<>(sets));

		ProofExtractor extractor = new ProofExtractor(internalContext,
				directoryPath + File.separatorChar + internalContext.getName());
		internalContext = internalContext.set(ProofObligation.class, extractor.getProofs());

		model = model.addContext(internalContext);
		if (seesNames.contains(internalContext.getName())) {
			sees.add(internalContext);
		}
		extractingContext = false;

	}

	@Override
	public void endDocument() throws SAXException {
		machine = machine.set(Event.class, new ModelElementList<>(events));
		ModelElementList<EventBInvariant> invs = new ModelElementList<>(inheritedInvariants);
		invs = invs.addMultiple(invariants);
		machine = machine.set(Invariant.class, invs);
		machine = machine.set(Machine.class, new ModelElementList<>(refines));
		machine = machine.set(Context.class, new ModelElementList<>(sees));
		machine = machine.set(Variable.class, new ModelElementList<>(variables));
		machine = machine.set(Variant.class, new ModelElementList<>(variant));
		machine = machine.set(BEvent.class, new ModelElementList<>(events));

		ProofExtractor proofExtractor = new ProofExtractor(machine,
				directoryPath + File.separatorChar + machine.getName());
		machine = machine.set(ProofObligation.class, proofExtractor.getProofs());
		model = model.addMachine(machine);

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

	public EventBMachine getMachine() {
		return machine;
	}

	public EventBModel getModel() {
		return model;
	}
}
