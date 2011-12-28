package de.prob.model;

import java.io.File;

import de.prob.ProBException;
import de.prob.animator.command.GetMachineObjectsCommand;
import de.prob.animator.command.ICommand;

public interface IModelFactory {
	ICommand getLoadCommand(File file, String name) throws ProBException;

	StateTemplate generate(GetMachineObjectsCommand getInfo);

}
