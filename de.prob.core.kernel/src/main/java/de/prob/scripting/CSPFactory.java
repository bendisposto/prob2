package de.prob.scripting;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.LoadCSPCommand;
import de.prob.animator.command.StartAnimationCommand;

public class CSPFactory {
	private final Provider<CSPModel> modelCreator;

	@Inject
	public CSPFactory(final Provider<CSPModel> modelProvider) {
		this.modelCreator = modelProvider;
	}

	public CSPModel load(final File f) throws IOException, BException {
		CSPModel cspModel = modelCreator.get();
		cspModel.init(f.getName());
		startAnimation(cspModel, f);
		return cspModel;
	}

	private void startAnimation(final CSPModel cspModel, final File f) {
		final ICommand loadcmd = new LoadCSPCommand(f.getAbsolutePath());
		cspModel.getStatespace().execute(loadcmd);
		cspModel.getStatespace().execute(new StartAnimationCommand());
		cspModel.getStatespace().setLoadcmd(loadcmd);
	}
}
