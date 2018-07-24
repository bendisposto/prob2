package de.prob.model.eventb.generate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.be4.eventbalg.core.parser.node.AAction;
import de.be4.eventbalg.core.parser.node.AAnticipatedConvergence;
import de.be4.eventbalg.core.parser.node.AConvergentConvergence;
import de.be4.eventbalg.core.parser.node.ADerivedGuard;
import de.be4.eventbalg.core.parser.node.AExtendedEventRefinement;
import de.be4.eventbalg.core.parser.node.AGuard;
import de.be4.eventbalg.core.parser.node.AOrdinaryConvergence;
import de.be4.eventbalg.core.parser.node.AParameter;
import de.be4.eventbalg.core.parser.node.ARefinesEventRefinement;
import de.be4.eventbalg.core.parser.node.AWitness;
import de.be4.eventbalg.core.parser.node.TComment;
import de.be4.eventbalg.core.parser.node.Token;

import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventModifier;
import de.prob.model.eventb.ModelGenerationException;
import de.prob.model.representation.ModelElementList;

import org.eventb.core.ast.extension.IFormulaExtension;

public class EventExtractor extends ElementExtractor {

	private EventModifier eventM;
	private ModelElementList<EventBMachine> machineRefines;

	public EventExtractor(final Event event,
			ModelElementList<EventBMachine> machineRefines,
			Set<IFormulaExtension> typeEnv, String comment) {
		super(typeEnv);
		this.machineRefines = machineRefines;
		eventM = new EventModifier(event, "INITIALISATION".equals(event.getName()), typeEnv);
		eventM = eventM.addComment(comment);
	}

	public Event getEvent() {
		return eventM.getEvent();
	}

	@Override
	public void caseAParameter(final AParameter node) {
		try {
			eventM = eventM.parameter(node.getName().getText(),
					getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAGuard(final AGuard node) {
		try {
			eventM = eventM.guard(node.getName().getText(), node.getPredicate()
					.getText(), false, getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseADerivedGuard(final ADerivedGuard node) {
		try {
			eventM = eventM.guard(node.getName().getText(), node.getPredicate()
					.getText(), true, getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAAction(final AAction node) {
		try {
			eventM = eventM.action(node.getName().getText(), node.getAction()
					.getText(), getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAWitness(final AWitness node) {
		try {
			eventM = eventM.witness(node.getName().getText(), node
					.getPredicate().getText(), getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseARefinesEventRefinement(ARefinesEventRefinement node) {
		if (machineRefines.isEmpty()) {
			throw new IllegalArgumentException(
					"Could not find machine refinement although event is marked as a refinement");
		}
		if (node.getNames().size() != 1) {
			throw new IllegalArgumentException(
					"The API currently only supports single refinement for events");
		}
		String name = node.getNames().get(0).getText();
		Event event = machineRefines.get(0).getEvent(name);
		if (event == null) {
			throw new IllegalArgumentException(
					"Could not find refined event with name " + name);
		}
		eventM = eventM.refines(event, false);
	}

	@Override
	public void caseAExtendedEventRefinement(final AExtendedEventRefinement node) {
		if (machineRefines.isEmpty()) {
			throw new IllegalArgumentException(
					"Could not find machine refinement although event is marked as a refinement");
		}
		String name = node.getName().getText();
		Event event = machineRefines.get(0).getEvent(name);
		if (event == null) {
			throw new IllegalArgumentException(
					"Could not find refined event with name " + name);
		}
		eventM = eventM.refines(event, true);
	}

	@Override
	public void caseAConvergentConvergence(AConvergentConvergence node) {
		eventM = eventM.setType(EventType.CONVERGENT);
	}

	@Override
	public void caseAOrdinaryConvergence(AOrdinaryConvergence node) {
		eventM = eventM.setType(EventType.ORDINARY);
	}

	@Override
	public void caseAAnticipatedConvergence(AAnticipatedConvergence node) {
		eventM = eventM.setType(EventType.ANTICIPATED);
	}

	public String getComment(List<TComment> comments) {
		return comments.stream().map(Token::getText).collect(Collectors.joining("\n"));
	}
}
