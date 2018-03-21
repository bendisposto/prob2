package de.prob.model.eventb.generate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.be4.eventbalg.core.parser.node.AAlgorithm;
import de.be4.eventbalg.core.parser.node.ADerivedInvariant;
import de.be4.eventbalg.core.parser.node.AEvent;
import de.be4.eventbalg.core.parser.node.AInvariant;
import de.be4.eventbalg.core.parser.node.ATypedVar;
import de.be4.eventbalg.core.parser.node.AVariable;
import de.be4.eventbalg.core.parser.node.AVariant;
import de.be4.eventbalg.core.parser.node.TComment;
import de.be4.eventbalg.core.parser.node.Token;

import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.MachineModifier;
import de.prob.model.eventb.ModelGenerationException;
import de.prob.model.eventb.algorithm.ast.Block;
import de.prob.model.representation.BEvent;

import org.eventb.core.ast.extension.IFormulaExtension;

public class MachineExtractor extends ElementExtractor {

	private MachineModifier machineM;

	public MachineExtractor(final MachineModifier machineM, final Set<IFormulaExtension> typeEnv) {
		super(typeEnv);
		this.machineM = machineM;
	}

	public EventBMachine getMachine() {
		return machineM.getMachine();
	}

	public MachineModifier getMachineModifier() {
		return machineM;
	}

	@Override
	public void caseAVariable(final AVariable node) {
		try {
			machineM = machineM.variable(node.getName().getText(), getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseATypedVar(ATypedVar node) {
		try {
			machineM = machineM.var(node.getName().getText(), node.getTypingpred().getText(), node.getInit().getText());
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAInvariant(final AInvariant node) {
		try {
			machineM = machineM.invariant(node.getName().getText(), node.getPredicate().getText(), false,
					getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseADerivedInvariant(final ADerivedInvariant node) {
		try {
			machineM = machineM.invariant(node.getName().getText(), node.getPredicate().getText(), true,
					getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAVariant(final AVariant node) {
		try {
			machineM = machineM.variant(node.getExpression().getText(), getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAEvent(final AEvent node) {
		EventExtractor eE = new EventExtractor(new Event(node.getName().getText(), EventType.ORDINARY, false),
				machineM.getMachine().getRefines(), typeEnv, getComment(node.getComments()));

		node.apply(eE);

		machineM = new MachineModifier(machineM.getMachine().addTo(BEvent.class, eE.getEvent()), typeEnv);
	}

	@Override
	public void caseAAlgorithm(final AAlgorithm node) {
		AlgorithmExtractor aE = new AlgorithmExtractor(typeEnv);
		Block algorithm = aE.extract(node);
		machineM = new MachineModifier(machineM.getMachine().addTo(Block.class, algorithm), typeEnv);
	}

	public String getComment(final List<TComment> comments) {
		return comments.stream().map(Token::getText).collect(Collectors.joining("\n"));
	}
}
