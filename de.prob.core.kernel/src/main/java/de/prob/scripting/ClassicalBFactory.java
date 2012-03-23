package de.prob.scripting;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.ProBException;
import de.prob.animator.command.GetInvariantsCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.StateSpace;
import de.prob.model.representation.ClassicalBMachine;
import de.prob.model.representation.LoadBProjectFromStringCommand;
import de.prob.model.representation.Predicate;

public class ClassicalBFactory {

	private final Provider<StateSpace> statespaceProvider;

	@Inject
	public ClassicalBFactory(final Provider<StateSpace> statespaceProvider) {
		this.statespaceProvider = statespaceProvider;
	}

	public ClassicalBMachine load(final File f) throws ProBException {
		ICommand loadCommand = new LoadBProjectCommand(f);
		return load(loadCommand);
	}

	public ClassicalBMachine load(final String s) throws ProBException {
		ICommand loadCommand = new LoadBProjectFromStringCommand(s);
		return load(loadCommand);
	}

	public ClassicalBMachine load(final ICommand loadCommand)
			throws ProBException {
		StateSpace stateSpace = statespaceProvider.get();
		ClassicalBMachine classicalBMachine = new ClassicalBMachine(stateSpace);

		GetInvariantsCommand getInvariantsCommand = new GetInvariantsCommand();
		stateSpace.execute(loadCommand, new StartAnimationCommand(),
				getInvariantsCommand);

		classicalBMachine.setInvariant(new Predicate(getInvariantsCommand
				.getInvariant()));

		return classicalBMachine;

	}

}
