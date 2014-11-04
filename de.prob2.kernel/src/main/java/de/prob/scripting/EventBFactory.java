package de.prob.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.LoadEventBFileCommand;
import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBDatabaseTranslator;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.statespace.StateSpace;

public class EventBFactory extends ModelFactory {

	private final Provider<EventBModel> modelProvider;

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider,
			final FileHandler fileHandler) {
		super(fileHandler);
		this.modelProvider = modelProvider;
	}

	public EventBModel load(final String file, final Map<String, String> prefs,
			final boolean loadVariables) {
		EventBModel model = modelProvider.get();

		new EventBDatabaseTranslator(model, file);

		return loadModel(model, getPreferences(model, prefs), loadVariables);
	}

	public static EventBModel loadModel(final EventBModel model,
			final Map<String, String> prefs, final boolean loadVariables) {
		List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

		for (Entry<String, String> pref : prefs.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		AbstractCommand loadcmd = new LoadEventBProjectCommand(
				new EventBModelTranslator(model));

		cmds.add(loadcmd);
		cmds.add(new StartAnimationCommand());
		StateSpace s = model.getStateSpace();
		s.execute(new ComposedCommand(cmds));
		s.setLoadcmd(loadcmd);

		if (loadVariables) {
			model.subscribeFormulasOfInterest();
		}
		return model;
	}

	public EventBModel loadModelFromEventBFile(final String fileName,
			final Map<String, String> prefs) throws IOException {
		EventBModel model = modelProvider.get();
		Pattern pattern = Pattern.compile("^package\\((.*?)\\)\\.");
		File file = new File(fileName);
		List<String> lines = readFile(file);
		String loadcmd = null;
		for (String string : lines) {
			Matcher m1 = pattern.matcher(string);
			if (m1.find()) {
				loadcmd = m1.group(1);
			}
		}
		model.setModelFile(file);
		model.isFinished();

		List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

		for (Entry<String, String> pref : prefs.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		StateSpace s = model.getStateSpace();
		s.execute(new ComposedCommand(cmds));

		LoadEventBFileCommand load = new LoadEventBFileCommand(loadcmd);
		s.execute(load);
		s.execute(new StartAnimationCommand());

		s.setLoadcmd(load);
		return model;
	}

	public final List<String> readFile(final File machine) throws IOException {
		ArrayList<String> res = new ArrayList<String>();
		FileInputStream fstream = new FileInputStream(machine);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					res.add(line);
				}
			}
			return res;
		} finally {
			fstream.close();
		}
	}
}
