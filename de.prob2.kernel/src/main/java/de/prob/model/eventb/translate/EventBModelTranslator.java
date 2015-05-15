package de.prob.model.eventb.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.eventb.ProofObligation;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Machine;
import de.prob.prolog.output.IPrologTermOutput;

public class EventBModelTranslator {
	List<EventBMachineTranslator> machineTranslators = new ArrayList<EventBMachineTranslator>();
	List<ContextTranslator> contextTranslators = new ArrayList<ContextTranslator>();
	List<ProofObligation> proofObligations = new ArrayList<ProofObligation>();
	private final TheoryTranslator theoryTranslator;
	private final EventBModel model;

	public EventBModelTranslator(final EventBModel model) {
		this.model = model;

		for (Machine machine : extractMachineHierarchy(model)) {
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

	private List<EventBMachine> extractMachineHierarchy(final EventBModel model) {
		AbstractElement mainComponent = model.getMainComponent();
		if (mainComponent instanceof Context) {
			return Collections.emptyList();
		}
		List<EventBMachine> machines = new ArrayList<EventBMachine>();
		if (mainComponent instanceof EventBMachine) {
			EventBMachine machine = (EventBMachine) mainComponent;
			machines.add(machine);
			machines.addAll(extractMachines(machine));
		}
		return machines;
	}

	private List<EventBMachine> extractMachines(final EventBMachine machine) {
		if (machine.getRefines().isEmpty()) {
			return Collections.emptyList();
		}
		List<EventBMachine> machines = new ArrayList<EventBMachine>();
		for (EventBMachine eventBMachine : machine.getRefines()) {
			machines.add(eventBMachine);
			machines.addAll(extractMachines(eventBMachine));
		}
		return machines;
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

		printPragmas(pto);

		pto.closeList();

		pto.printVariable("_Error");
		pto.closeTerm();
	}

	private void printPragmas(final IPrologTermOutput pto) {
		for (Machine machine : model.getChildrenOfType(Machine.class)) {
			EventBMachine ebM = (EventBMachine) machine;
			for (EventBVariable var : ebM.getVariables()) {
				if (var.hasUnit()) {
					pto.openTerm("pragma");
					pto.printAtom("unit");
					pto.printAtom(machine.getName());
					pto.printAtom(var.getName());
					pto.openList();
					pto.printAtom(var.getUnit());
					pto.closeList();
					pto.closeTerm();
				}
			}
		}

		for (Context context : model.getChildrenOfType(Context.class)) {
			for (EventBConstant constant : context.getConstants()) {
				if (constant.hasUnit()) {
					pto.openTerm("pragma");
					pto.printAtom("unit");
					pto.printAtom(context.getName());
					pto.printAtom(constant.getName());
					pto.openList();
					pto.printAtom(constant.getUnit());
					pto.closeList();
					pto.closeTerm();
				}
			}
		}
	}

}
