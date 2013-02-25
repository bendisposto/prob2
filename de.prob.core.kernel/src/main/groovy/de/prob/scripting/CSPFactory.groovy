package de.prob.scripting

import java.util.HashMap.Entry

import com.google.inject.Inject
import com.google.inject.Provider

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.animator.command.ICommand
import de.prob.animator.command.LoadCSPCommand
import de.prob.animator.command.SetPreferenceCommand
import de.prob.animator.command.StartAnimationCommand

class CSPFactory {

	private final Provider<CSPModel> modelCreator;

	@Inject
	public CSPFactory(final Provider<CSPModel> modelProvider) {
		this.modelCreator = modelProvider;
	}

	public CSPModel load(final File f, Map<String, String> prefs) throws IOException, BException {
		CSPModel cspModel = modelCreator.get();

		for (Entry<String,String> pref : prefs.entrySet()) {
			cspModel.getStatespace().execute(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		cspModel.init(readFile(f),f);
		startAnimation(cspModel, f);
		return cspModel;
	}

	private String readFile(File f) {
		return f.getText();
	}

	private void startAnimation(final CSPModel cspModel, final File f) {
		final ICommand loadcmd = new LoadCSPCommand(f.getAbsolutePath());
		cspModel.getStatespace().execute(loadcmd);
		cspModel.getStatespace().execute(new StartAnimationCommand());
		cspModel.getStatespace().setLoadcmd(loadcmd);
	}
}
