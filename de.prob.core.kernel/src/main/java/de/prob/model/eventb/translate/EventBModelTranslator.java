package de.prob.model.eventb.translate;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.proof.ProofObligation;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.representation.Machine;
import de.prob.prolog.output.IPrologTermOutput;

public class EventBModelTranslator {
	List<EventBMachineTranslator> machineTranslators = new ArrayList<EventBMachineTranslator>();
	List<ContextTranslator> contextTranslators = new ArrayList<ContextTranslator>();
	List<ProofObligation> proofObligations = new ArrayList<ProofObligation>();
	private final TheoryTranslator theoryTranslator;

	public EventBModelTranslator(final EventBModel model) {

		for (Machine machine : model.getChildrenOfType(Machine.class)) {
			EventBMachine ebM = (EventBMachine) machine;
			machineTranslators.add(new EventBMachineTranslator(ebM));
			proofObligations.addAll(ebM.getProofs());
		}

		for (Context context : model.getChildrenOfType(Context.class)) {
			contextTranslators.add(new ContextTranslator(context));
			proofObligations.addAll(context.getProofs());
		}

		theoryTranslator = new TheoryTranslator(
				model.getChildrenOfType(Theory.class));
	}

	public void printProlog(final IPrologTermOutput pto) {
		RodinPosPrinter labelPrinter = new RodinPosPrinter();

		List<Node> machineNodes = new ArrayList<Node>();
		List<Node> contextNodes = new ArrayList<Node>();
		for (EventBMachineTranslator trans : machineTranslators) {
			machineNodes.add(trans.translateMachine());
			labelPrinter.addNodeInfos(trans.getNodeInfos());
		}
		for (ContextTranslator t : contextTranslators) {
			contextNodes.add(t.translateContext());
			labelPrinter.addNodeInfos(t.getNodeInfos());
		}

		ASTProlog printer = new ASTProlog(pto, labelPrinter);
		pto.openTerm("load_event_b_project");
		pto.openList();
		for (Node node : machineNodes) {
			node.apply(printer);
		}
		pto.closeList();

		pto.openList();
		for (Node node : contextNodes) {
			node.apply(printer);
		}
		pto.closeList();

		pto.openList();
		pto.openTerm("exporter_version");
		pto.printNumber(3);
		pto.closeTerm();

		for (ProofObligation po : proofObligations) {
			po.toProlog(pto);
		}

		theoryTranslator.toProlog(pto);

		pto.closeList();

		pto.printVariable("_Error");
		pto.closeTerm();
	}

}
