package de.prob.scripting;

import java.util.regex.Matcher
import java.util.regex.Pattern

import com.google.inject.Inject
import com.google.inject.Provider

import de.prob.animator.command.LoadEventBFileCommand
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.translate.EventBDatabaseTranslator
import de.prob.model.representation.AbstractElement
import de.prob.statespace.StateSpace

public class EventBFactory implements ModelFactory<EventBModel> {

	private final StateSpaceProvider ssProvider
	private final Provider<EventBModel> modelCreator

	@Inject
	public EventBFactory(final Provider<EventBModel> modelCreator, StateSpaceProvider ssProvider) {
		this.ssProvider = ssProvider
		this.modelCreator = modelCreator
	}

	@Override
	public ExtractedModel<EventBModel> extract(String modelPath) throws IOException,
	ModelTranslationError {
		EventBModel model = modelCreator.get();
		def validFileName = getValidFileName(modelPath)
		EventBDatabaseTranslator translator = new EventBDatabaseTranslator(model, validFileName);
		new ExtractedModel<EventBModel>(translator.getModel(),translator.getMainComponent())
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


	public StateSpace loadModelFromEventBFile(final String fileName,
			final Map<String, String> prefs) throws IOException {
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
		return ssProvider.loadFromCommand(model, new DummyElement(componentName), prefs, new LoadEventBFileCommand(loadcmd))
	}

	public final List<String> readFile(final File machine) throws IOException {
		def lines = []
		machine.eachLine { lines << it }
		lines
	}

	public EventBModel extractModelFromZip(final String zipfile) throws IOException {
		final File tempdir = createTempDir()
		new FileHandler().extractZip(zipfile,tempdir.getAbsolutePath())

		def pattern = Pattern.compile(".*.bcc\$|.*.bcm\$")
		def modelFiles = []
		tempdir.traverse(nameFilter: pattern) { f -> modelFiles << f }
		if (modelFiles.size() == 0) {
			tempdir.deleteDir()
			throw new IllegalArgumentException("No static checked Event-B files were found in that zip archive!")
		}
		EventBModel model = modelCreator.get();
		modelFiles.each { File f ->
			String modelPath = f.getAbsolutePath()
			String name = modelPath.substring(modelPath.lastIndexOf(File.separatorChar.toString()) + 1, modelPath.lastIndexOf("."))
			if (!model.getComponent(name)) {
				EventBDatabaseTranslator translator = new EventBDatabaseTranslator(model, modelPath);
				model = translator.getModel()
			}
		}
		return model
	}

	private File createTempDir() {
		final File tempdir = File.createTempDir("eventb-model","")

		// the temporary directory will be deleted on shutdown of the JVM
		Runtime.getRuntime().addShutdownHook(new Thread("EventB TempDir Deleter") {
					public void run()
					{
						tempdir.deleteDir()
					}
				});
		tempdir
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
