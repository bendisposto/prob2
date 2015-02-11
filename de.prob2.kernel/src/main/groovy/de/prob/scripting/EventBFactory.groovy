package de.prob.scripting;

import java.util.Map.Entry
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipInputStream

import com.google.inject.Inject
import com.google.inject.Provider

import de.prob.animator.command.AbstractCommand
import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.LoadEventBFileCommand
import de.prob.animator.command.LoadEventBProjectCommand
import de.prob.animator.command.SetPreferenceCommand
import de.prob.animator.command.StartAnimationCommand
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.translate.EventBDatabaseTranslator
import de.prob.model.eventb.translate.EventBModelTranslator
import de.prob.model.representation.AbstractElement
import de.prob.statespace.StateSpace

public class EventBFactory extends ModelFactory<EventBModel> {

	@Inject
	public EventBFactory(final Provider<EventBModel> modelCreator,
	final FileHandler fileHandler) {
		super(modelCreator, fileHandler, LoadClosures.getEVENTB());
	}

	@Override
	public EventBModel load(final String modelPath, final Map<String, String> prefs,
			final Closure<Object> loader) throws IOException, ModelTranslationError {
		EventBModel model = modelCreator.get();

		new EventBDatabaseTranslator(model, getValidFileName(modelPath));

		return loadModel(model, getPreferences(model, prefs), loader);
	}

	private String getValidFileName(String fileName) {
		if (fileName.endsWith(".buc")) {
			fileName = fileName.replaceAll("\\.buc\$", ".bcc");
		}
		if (fileName.endsWith(".bum")) {
			fileName = fileName.replaceAll("\\.bum\$", ".bcm");
		}
		if (!(fileName.endsWith(".bcc") || fileName.endsWith(".bcm"))) {
			throw new IllegalArgumentException("$fileName is not a valid Event-B file")
		}
		fileName
	}


	/**
	 * Loads the specified EventBModel, sets the specified ProB Preferences, and then
	 * executes the user specified loader to
	 * @param model to load
	 * @param prefs ProB preferences
	 * @param loader actions to take place after loading
	 * @return the same model after the loading process
	 */
	public static EventBModel loadModel(final EventBModel model,
			final Map<String, String> prefs, final Closure<Object> loader) throws IOException, ModelTranslationError {
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

		loader(model)
		return model;
	}

	public EventBModel loadModelFromEventBFile(final String fileName,
			final Map<String, String> prefs, final Closure loadClosure) throws IOException {
		EventBModel model = modelCreator.get();
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
		if (loadcmd == null) {
			throw new IllegalArgumentException("$fileName contained no valid Event-B Load command")
		}

		String componentName = file.getName().replaceAll("\\.eventb\$", "")

		model.setMainComponent(new DummyElement(componentName))
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

		loadClosure(model)
		return model;
	}

	public final List<String> readFile(final File machine) throws IOException {
		def lines = []
		machine.eachLine { lines << it }
		lines
	}

	public EventBModel loadModelFromZip(final String zipfile, String componentName,
			final Map<String, String> prefs, Closure loader) throws IOException {
		File.metaClass.unzip = { String dest ->
			//in metaclass added methods, 'delegate' is the object on which
			//the method is called. Here it's the file to unzip
			def result = new ZipInputStream(new FileInputStream(delegate))
			def destFile = new File(dest)
			if(!destFile.exists()){
				destFile.mkdir();
			}
			result.withStream{
				def entry
				while(entry = result.nextEntry){
					if (!entry.isDirectory()){
						new File(dest + File.separator + entry.name).parentFile?.mkdirs()
						def output = new FileOutputStream(dest + File.separator
								+ entry.name)
						output.withStream{
							int len = 0;
							byte[] buffer = new byte[4096]
							while ((len = result.read(buffer)) > 0){
								output.write(buffer, 0, len);
							}
						}
					}
					else {
						new File(dest + File.separator + entry.name).mkdir()
					}
				}
			}

		}

		def pattern = Pattern.compile(".*${componentName}.bcc|.*${componentName}.bcm")

		File zip = new File(zipfile)
		final File tempdir = File.createTempDir("eventb-model","")

		zip.unzip(tempdir.getAbsolutePath())

		// the temporary directory will be deleted on shutdown of the JVM
		Runtime.getRuntime().addShutdownHook(new Thread()
				{
					public void run()
					{
						tempdir.deleteDir()
					}
				});


		def modelFiles = []
		tempdir.traverse(nameFilter: pattern) { f -> modelFiles << f }
		if (modelFiles.size() != 1) {
			tempdir.deleteDir()
			throw new IllegalArgumentException("The component name should reference exactly one component in the model.")
		}

		return load(modelFiles[0].getAbsolutePath(), prefs, loader);
	}

	private class DummyElement extends AbstractElement {
		def String name;

		def DummyElement(String name) {
			this.name = name
		}

		def String toString() {
			name
		}
	}
}
