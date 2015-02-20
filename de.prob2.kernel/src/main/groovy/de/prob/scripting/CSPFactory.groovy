package de.prob.scripting


import com.google.inject.Inject
import com.google.inject.Provider

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.LoadCSPCommand
import de.prob.animator.command.SetPreferenceCommand
import de.prob.animator.command.StartAnimationCommand
import de.prob.model.representation.CSPModel

class CSPFactory extends ModelFactory<CSPModel> {


	@Inject
	public CSPFactory(final Provider<CSPModel> modelCreator, FileHandler fileHandler) {
		super(modelCreator, fileHandler, LoadClosures.EMPTY)
	}

	@Override
	public CSPModel load(final String modelPath, Map<String, String> prefs, Closure<Object> loadClosure) throws IOException, ModelTranslationError {
		CSPModel cspModel = modelCreator.get()
		File f = new File(modelPath)
		cspModel.init(readFile(f),f)
		startAnimation(cspModel, f, getPreferences(cspModel, prefs))
		loadClosure(cspModel)
		return cspModel;
	}

	@Deprecated
	public CSPModel load(final File f, Map<String, String> prefs, Closure loadClosure) throws IOException, BException {
		return load(f.getAbsolutePath(), prefs, loadClosure)
	}

	private String readFile(File f) {
		return f.getText();
	}

	private void startAnimation(final CSPModel cspModel, final File f, final Map<String, String> prefs) {
		def cmds = [];

		prefs.each { k,v -> cmds << new SetPreferenceCommand(k, v) }

		cmds << new LoadCSPCommand(f.getAbsolutePath());
		cmds << new StartAnimationCommand()

		cspModel.getStateSpace().execute(new ComposedCommand(cmds));
	}
}
