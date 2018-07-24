package de.prob.model.eventb.generate;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.be4.eventbalg.core.parser.node.AContextParseUnit;
import de.be4.eventbalg.core.parser.node.AMachineParseUnit;
import de.be4.eventbalg.core.parser.node.AProcedureParseUnit;
import de.be4.eventbalg.core.parser.node.TComment;
import de.be4.eventbalg.core.parser.node.TIdentifierLiteral;
import de.be4.eventbalg.core.parser.node.Token;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.ContextModifier;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.MachineModifier;
import de.prob.model.eventb.ModelModifier;
import de.prob.model.eventb.algorithm.Procedure;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class ComponentExtractor extends ElementExtractor {

	private ModelModifier modelM;

	public ComponentExtractor(final ModelModifier modelM) {
		super(modelM.typeEnvironment);
		this.modelM = modelM;
	}

	public EventBModel getModel() {
		return modelM.getModel();
	}

	public ModelModifier getModelModifier() {
		return modelM;
	}

	@Override
	public void caseAMachineParseUnit(final AMachineParseUnit node) {
		String name = node.getName().getText();
		MachineModifier machineM = new MachineModifier(new EventBMachine(name),
				typeEnv);
		ModelElementList<Context> seen = new ModelElementList<>();
		for (TIdentifierLiteral contextName : node.getSeenNames()) {
			String cName = contextName.getText();
			AbstractElement context = getContext(cName);
			seen = seen.addElement((Context) context);
		}
		machineM = machineM.setSees(seen);
		if (node.getRefinesNames().size() == 1) {
			String mname = node.getRefinesNames().getFirst().getText();
			EventBMachine machine = getMachine(mname);
			machineM = machineM.setRefines(machine);
		} else if (node.getRefinesNames().size() > 1) {
			throw new IllegalArgumentException(
					"Machines can only refine one abstract machine. Found "
							+ node.getRefinesNames().size()
							+ " refined machines");
		}
		machineM = machineM.addComment(getComment(node.getComments()));
		MachineExtractor mE = new MachineExtractor(machineM, typeEnv);
		node.apply(mE);
		modelM = modelM.addMachine(mE.getMachine());

	}

	@Override
	public void caseAContextParseUnit(final AContextParseUnit node) {
		String name = node.getName().getText();
		ContextModifier contextM = new ContextModifier(new Context(name),
				typeEnv);
		if (node.getExtendsNames().size() == 1) {
			String cName = node.getExtendsNames().getFirst().getText();
			Context ctx = getContext(cName);
			contextM = contextM.setExtends(ctx);
		} else if (node.getExtendsNames().size() > 1) {
			throw new IllegalArgumentException(
					"Contexts can only refine one abstract context. Found "
							+ node.getExtendsNames().size()
							+ " extended machines");
		}
		contextM = contextM.addComment(getComment(node.getComments()));
		ContextExtractor cE = new ContextExtractor(contextM, typeEnv);
		node.apply(cE);
		modelM = modelM.addContext(cE.getContext());
	}

	@Override
	public void caseAProcedureParseUnit(final AProcedureParseUnit node) {
		String name = node.getName().getText();
		LinkedList<TIdentifierLiteral> seen = node.getSeen();
		Context ctx = null;
		if (node.getSeen().size() == 1) {
			String cName = seen.getFirst().getText();
			ctx = getContext(cName);
		} else if (node.getSeen().size() > 1) {
			throw new IllegalArgumentException("Error in " + name
					+ " definition: " + node.getStartPos()
					+ " only one context may be seen by a procedure");
		}
		Procedure procedure = new Procedure(name, ctx, typeEnv);
		ProcedureExtractor pE = new ProcedureExtractor(procedure, node, typeEnv);
		modelM = modelM.addProcedure(pE.getProcedure());

	}

	private Context getContext(final String cName) {
		Context ctx = modelM.getModel().getContext(cName);
		if (ctx == null) {
			throw new IllegalArgumentException(
					"Tried to find context with name " + cName
							+ ", but could not find it.");
		}
		return ctx;
	}

	private EventBMachine getMachine(final String mname) {
		EventBMachine machine = modelM.getModel().getMachine(mname);
		if (machine == null) {
			throw new IllegalArgumentException(
					"Tried to find machine with name " + mname
							+ ", but could not find it.");
		}
		return machine;
	}

	public String getComment(final List<TComment> comments) {
		return comments.stream().map(Token::getText).collect(Collectors.joining("\n"));
	}
}
