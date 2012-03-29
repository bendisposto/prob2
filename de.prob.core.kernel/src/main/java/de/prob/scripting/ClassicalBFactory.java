package de.prob.scripting;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.ProBException;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.StartAnimationCommand;
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
		ClassicalBMachine classicalBMachine = new ClassicalBMachine(stateSpace,
				loadCommand.getNodeIdMapping());

		// GetInvariantsCommand getInvariantsCommand = new
		// GetInvariantsCommand();
		stateSpace.execute(loadCommand, new StartAnimationCommand());

		// List<StringWithLocation> list = getInvariantsCommand.getInvariant();
		// for (StringWithLocation string : list) {
		// System.out.println("@" + string);
		// }

		return classicalBMachine;

	}

}
