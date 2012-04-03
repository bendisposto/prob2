package de.prob.scripting;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.prob.ProBException;
import de.prob.animator.command.notImplemented.LoadBProjectCommand;
import de.prob.animator.command.notImplemented.StartAnimationCommand;
import de.prob.animator.command.representation.GetInvariantsCommand;
import de.prob.animator.command.representation.GetModelNameCommand;
import de.prob.model.StateSpace;
import de.prob.model.representation.ClassicalBMachine;

public class ClassicalBFactory {

	private final Provider<StateSpace> statespaceProvider;

	@Inject
	public ClassicalBFactory(final Provider<StateSpace> statespaceProvider) {
		this.statespaceProvider = statespaceProvider;
	}

	public ClassicalBMachine load(final File f) throws ProBException {
		LoadBProjectCommand loadCommand = new LoadBProjectCommand(f);
		return load(loadCommand);
	}

	public ClassicalBMachine load(final LoadBProjectCommand loadCommand)
			throws ProBException {
		StateSpace stateSpace = statespaceProvider.get();
		stateSpace.execute(loadCommand);
		NodeIdAssignment nodeIdMapping = loadCommand.getNodeIdMapping();
		ClassicalBMachine classicalBMachine = new ClassicalBMachine(stateSpace,
				nodeIdMapping);

		GetInvariantsCommand getInvariantsCommand = new GetInvariantsCommand(
				nodeIdMapping);
		GetModelNameCommand getModelNameCommand = new GetModelNameCommand();
		stateSpace.execute(new StartAnimationCommand(), getInvariantsCommand,
				getModelNameCommand);

		classicalBMachine.addInvariants(getInvariantsCommand.getInvariants());
		classicalBMachine.setName(getModelNameCommand.getName());

		return classicalBMachine;

	}
}
