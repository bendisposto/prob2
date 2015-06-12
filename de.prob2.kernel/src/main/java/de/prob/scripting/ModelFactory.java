package de.prob.scripting;

import groovy.lang.Closure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.Main;
import de.prob.animator.IAnimator;
import de.prob.animator.command.GetCurrentPreferencesCommand;
import de.prob.model.representation.AbstractModel;

public abstract class ModelFactory<T extends AbstractModel> {

	/**
	 * Name of file in which the preferences are saved. Currently
	 * "prob2preferences"
	 */
	public final String PREFERENCE_FILE_NAME = "prob2preferences";
	private final FileHandler handler;
	protected final Closure<Object> defaultClosure;
	protected final Provider<T> modelCreator;

	@Inject
	public ModelFactory(final Provider<T> modelCreator,
			final FileHandler handler, final Closure<Object> defaultClosure) {
		this.modelCreator = modelCreator;
		this.handler = handler;
		this.defaultClosure = defaultClosure;
	}

	public abstract T extract(final String fileName) throws IOException,
			ModelTranslationError;

	public T load(final String fileName, final Map<String, String> preferences)
			throws IOException, ModelTranslationError {
		return load(fileName, preferences, defaultClosure);
	}

	/**
	 * This method loads a machine from file, parses all machines, starts the
	 * animation, and returns the created model
	 * 
	 * @param fileName
	 *            String path to the machine to be loaded.
	 * @param preferences
	 *            map of ProB preferences
	 * @param loader
	 *            actions to take place after the loading process
	 * @return model generated from the specified file.
	 * @throws IOException
	 * @throws BException
	 * @throws ModelTranslationError
	 */
	public abstract T load(String fileName, Map<String, String> preferences,
			Closure<Object> loader) throws IOException, ModelTranslationError;

	/**
	 * Finds preferences that are specified globally, locally, and for this
	 * particular instance. Creates a map of preferences to load using the
	 * correct difference preference specifications in the correct order (the
	 * preference for this instance are most important, then the local
	 * preferences, then global preferences)
	 * 
	 * @param model
	 *            for which the local preferences should be found
	 * @param prefsForInstance
	 *            specific preferences that will be loaded only for this
	 *            instance
	 * @return map of all preferences found.
	 */
	public Map<String, String> getPreferences(final AbstractModel model,
			final Map<String, String> prefsForInstance) {
		Map<String, String> preferences = getGlobalPreferences(model
				.getStateSpace());

		preferences.putAll(getLocalPreferences(model.getModelDirPath()));
		preferences.putAll(prefsForInstance);

		return preferences;
	}

	/**
	 * Checks to see if there is a file with file name
	 * {@link ModelFactory#PREFERENCE_FILE_NAME} in the
	 * {@link Main#getProBDirectory()} file. If there is, the method attempts to
	 * read the default global preferences out of this file via
	 * {@link FileHandler#getMapOfStrings(String)}. If no file exists, the
	 * IAnimator proB is contacted to determine the current default preferences,
	 * and these are written to the preference file via
	 * {@link FileHandler#setContent(String, Object)} .
	 * 
	 * @param proB
	 *            IAnimator which will be contacted in the case that no
	 *            preference file exists in the home file
	 * @return Map of default preferences
	 */
	private Map<String, String> getGlobalPreferences(final IAnimator proB) {
		String preferenceFileName = Main.getProBDirectory()
				+ PREFERENCE_FILE_NAME;
		Map<String, String> prefs = handler.getMapOfStrings(preferenceFileName);

		if (prefs == null) {
			GetCurrentPreferencesCommand cmd = new GetCurrentPreferencesCommand();
			proB.execute(cmd);
			prefs = cmd.getPreferences();
			handler.setContent(preferenceFileName, prefs);
		}
		return prefs;
	}

	/**
	 * Attempts to find preferences saved locally.
	 * 
	 * @param dirName
	 *            of the directory in which the preferences are stored locally.
	 * @return map of preferences saved locally
	 */
	private Map<String, String> getLocalPreferences(final String dirName) {
		String preferenceFileName = dirName + PREFERENCE_FILE_NAME;
		Map<String, String> prefs = handler.getMapOfStrings(preferenceFileName);

		if (prefs == null) {
			prefs = new HashMap<String, String>();
			handler.setContent(preferenceFileName, prefs);
		}
		return prefs;
	}
}
