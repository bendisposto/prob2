package de.prob.model.eventb.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.parboiled.common.Tuple2;

import de.be4.classicalb.core.parser.node.AAnticipatedEventstatus;
import de.be4.classicalb.core.parser.node.AConvergentEventstatus;
import de.be4.classicalb.core.parser.node.AEvent;
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit;
import de.be4.classicalb.core.parser.node.AEventsModelClause;
import de.be4.classicalb.core.parser.node.AInvariantModelClause;
import de.be4.classicalb.core.parser.node.AOrdinaryEventstatus;
import de.be4.classicalb.core.parser.node.ARefinesModelClause;
import de.be4.classicalb.core.parser.node.ASeesModelClause;
import de.be4.classicalb.core.parser.node.ATheoremsModelClause;
import de.be4.classicalb.core.parser.node.AVariablesModelClause;
import de.be4.classicalb.core.parser.node.AVariantModelClause;
import de.be4.classicalb.core.parser.node.AWitness;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PEvent;
import de.be4.classicalb.core.parser.node.PEventstatus;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PModelClause;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSubstitution;
import de.be4.classicalb.core.parser.node.PWitness;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.eventb.EventParameter;
import de.prob.model.eventb.Witness;

public class EventBMachineTranslator {

	private final EventBMachine machine;
	private final Map<Node, Tuple2<String, String>> nodeInfos = new HashMap<Node, Tuple2<String, String>>();

	public EventBMachineTranslator(final EventBMachine machine) {
		this.machine = machine;
	}

	public Map<Node, Tuple2<String, String>> getNodeInfos() {
		return nodeInfos;
	}

	public Node translateMachine() {
		AEventBModelParseUnit ast = new AEventBModelParseUnit();
		ast.setName(new TIdentifierLiteral(machine.getName()));
		List<PModelClause> clauses = new ArrayList<PModelClause>();

		clauses.add(processContexts());

		ARefinesModelClause refines = processRefines();
		if (refines != null) {
			clauses.add(refines);
		}

		clauses.add(processVariables());
		clauses.addAll(processInvariantsAndTheorems());

		AVariantModelClause variant = processVariant();
		if (variant != null) {
			clauses.add(variant);
		}

		clauses.add(processEvents());

		ast.setModelClauses(clauses);
		return ast;
	}

	private ASeesModelClause processContexts() {
		List<Context> sees = machine.getSees();
		List<TIdentifierLiteral> contextNames = new ArrayList<TIdentifierLiteral>();
		for (Context context : sees) {
			contextNames.add(new TIdentifierLiteral(context.getName()));
		}
		return new ASeesModelClause(contextNames);
	}

	private ARefinesModelClause processRefines() {
		List<EventBMachine> refines = machine.getRefines();
		if (!refines.isEmpty()) {
			return new ARefinesModelClause(new TIdentifierLiteral(refines
					.get(0).getName()));
		}
		return null;
	}

	private AVariablesModelClause processVariables() {
		List<PExpression> identifiers = new ArrayList<PExpression>();
		for (EventBVariable eventBVariable : machine.getVariables()) {
			identifiers.add((PExpression) ((EventB) eventBVariable
					.getExpression()).getAst());
		}

		return new AVariablesModelClause(identifiers);
	}

	private List<PModelClause> processInvariantsAndTheorems() {
		List<PModelClause> invsAndTheorems = new ArrayList<PModelClause>();
		List<PPredicate> invs = new ArrayList<PPredicate>();
		List<PPredicate> thms = new ArrayList<PPredicate>();

		List<EventBInvariant> allInvs = machine.getAllInvariants();
		for (EventBInvariant ebInv : allInvs) {
			PPredicate ppred = (PPredicate) ((EventB) ebInv.getPredicate())
					.getAst();
			nodeInfos.put(ppred, new Tuple2<String, String>(machine.getName(),
					ebInv.getName()));
			if (ebInv.isTheorem()) {
				thms.add(ppred);
			} else {
				invs.add(ppred);
			}
		}

		invsAndTheorems.add(new AInvariantModelClause(invs));
		invsAndTheorems.add(new ATheoremsModelClause(thms));
		return invsAndTheorems;
	}

	private AVariantModelClause processVariant() {
		if (machine.getVariant() != null) {
			EventB expression = (EventB) machine.getVariant().getExpression();
			return new AVariantModelClause((PExpression) expression.getAst());
		}
		return null;
	}

	private AEventsModelClause processEvents() {
		List<PEvent> events = new ArrayList<PEvent>();
		for (Event e : machine.getEvents()) {
			AEvent event = new AEvent();
			event.setEventName(new TIdentifierLiteral(e.getName()));
			event.setStatus(extractEventStatus(e));
			nodeInfos.put(event, new Tuple2<String, String>(machine.getName(),
					e.getName()));

			List<TIdentifierLiteral> refined = new ArrayList<TIdentifierLiteral>();
			for (Event ref : e.getRefines()) {
				refined.add(new TIdentifierLiteral(ref.getName()));
			}
			event.setRefines(refined);

			List<PExpression> params = new ArrayList<PExpression>();
			for (EventParameter eventParameter : e.getParameters()) {
				PExpression pExpression = (PExpression) eventParameter
						.getExpression().getAst();
				nodeInfos.put(pExpression,
						new Tuple2<String, String>(machine.getName(),
								eventParameter.getName()));
				params.add(pExpression);
			}
			event.setVariables(params);

			List<PPredicate> guards = new ArrayList<PPredicate>();
			List<PPredicate> thms = new ArrayList<PPredicate>();
			for (EventBGuard eventBGuard : e.getGuards()) {
				PPredicate ppred = (PPredicate) ((EventB) eventBGuard
						.getPredicate()).getAst();
				nodeInfos.put(ppred,
						new Tuple2<String, String>(machine.getName(),
								eventBGuard.getName()));
				if (eventBGuard.isTheorem()) {
					thms.add(ppred);
				} else {
					guards.add(ppred);
				}
			}
			event.setGuards(guards);
			event.setTheorems(thms);

			List<PWitness> witnesses = new ArrayList<PWitness>();
			for (Witness witness : e.getWitnesses()) {
				PPredicate ppred = (PPredicate) witness.getPredicate().getAst();
				nodeInfos.put(
						ppred,
						new Tuple2<String, String>(machine.getName(), witness
								.getName()));
				witnesses.add(new AWitness(new TIdentifierLiteral(witness
						.getName()), ppred));
			}
			event.setWitness(witnesses);

			List<PSubstitution> actions = new ArrayList<PSubstitution>();
			for (EventBAction eventBAction : e.getActions()) {
				PSubstitution psub = (PSubstitution) ((EventB) eventBAction
						.getCode()).getAst();
				nodeInfos.put(psub,
						new Tuple2<String, String>(machine.getName(),
								eventBAction.getName()));
				actions.add(psub);
			}
			event.setAssignments(actions);
			events.add(event);
		}
		return new AEventsModelClause(events);
	}

	private PEventstatus extractEventStatus(final Event e) {
		PEventstatus result = null;
		switch (e.getType()) {
		case ORDINARY:
			result = new AOrdinaryEventstatus();
			break;
		case ANTICIPATED:
			result = new AAnticipatedEventstatus();
			break;
		case CONVERGENT:
			result = new AConvergentEventstatus();
			break;
		default:
			break;
		}
		return result;
	}
}
