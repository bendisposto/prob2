package de.prob.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.LoadEventBFileCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBDatabaseTranslator;
import de.prob.model.representation.AbstractElement;
import de.prob.statespace.StateSpace;

import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class EventBFactory implements ModelFactory<EventBModel> {
	private final StateSpaceProvider ssProvider;
	private final Provider<EventBModel> modelCreator;

	@Inject
	public EventBFactory(final Provider<EventBModel> modelCreator, final StateSpaceProvider ssProvider) {
		this.ssProvider = ssProvider;
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<EventBModel> extract(String modelPath) throws IOException, ModelTranslationError {
		final EventBModel model = modelCreator.get();
		final String validFileName = getValidFileName(modelPath);
		final EventBDatabaseTranslator translator = new EventBDatabaseTranslator(model, validFileName);
		return new ExtractedModel<>(translator.getModel(), translator.getMainComponent());
	}

	private String getValidFileName(String fileName) {
		if (fileName.endsWith(".buc")) {
			fileName = fileName.replaceAll("\\.buc$", ".bcc");
		}
		if (fileName.endsWith(".bum")) {
			fileName = fileName.replaceAll("\\.bum$", ".bcm");
		}
		if (!(fileName.endsWith(".bcc") || fileName.endsWith(".bcm"))) {
			throw new IllegalArgumentException(fileName + " is not a valid Event-B file");
		}
		return fileName;
	}

	public StateSpace loadModelFromEventBFile(final String fileName, final Map<String, String> prefs) throws IOException {
		final EventBModel model = modelCreator.get();
		final Pattern pattern = Pattern.compile("^package\\((.*?)\\)\\.");
		final File file = new File(fileName);
		final List<String> lines = readFile(file);
		String loadcmd = null;
		for (final String string : lines) {
			final Matcher m1 = pattern.matcher(string);
			if (m1.find()) {
				loadcmd = m1.group(1);
			}
		}
		if (loadcmd == null) {
			throw new IllegalArgumentException(fileName + " contained no valid Event-B Load command");
		}

		final String componentName = file.getName().replaceAll("\\.eventb$", "");
		return ssProvider.loadFromCommand(model, new DummyElement(componentName), prefs, new LoadEventBFileCommand(loadcmd));
	}

	public final List<String> readFile(final File machine) throws IOException {
		try (final Stream<String> lines = Files.lines(machine.toPath())) {
			return lines.collect(Collectors.toList());
		}
	}

	public EventBModel extractModelFromZip(final String zipfile) throws IOException {
		final File tempdir = createTempDir();
		try (final InputStream is = new FileInputStream(zipfile)) {
			FileHandler.extractZip(is, tempdir.toPath());
		}

		final Pattern pattern = Pattern.compile(".*.bcc$|.*.bcm$");
		final List<File> modelFiles = new ArrayList<>();
		Files.walkFileTree(tempdir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
				if (pattern.matcher(file.getFileName().toString()).matches()) {
					modelFiles.add(file.toFile());
				}
				return FileVisitResult.CONTINUE;
			}
		});
		if (modelFiles.isEmpty()) {
			ResourceGroovyMethods.deleteDir(tempdir);
			throw new IllegalArgumentException("No static checked Event-B files were found in that zip archive!");
		}
		EventBModel model = modelCreator.get();
		for (File f : modelFiles) {
			String modelPath = f.getAbsolutePath();
			String name = modelPath.substring(modelPath.lastIndexOf(File.separatorChar) + 1, modelPath.lastIndexOf("."));
			if (model.getComponent(name) == null) {
				EventBDatabaseTranslator translator = new EventBDatabaseTranslator(model, modelPath);
				model = translator.getModel();
			}
		}
		return model;
	}

	private File createTempDir() throws IOException {
		final File tempdir = Files.createTempDirectory("eventb-model").toFile();
		// the temporary directory will be deleted on shutdown of the JVM
		Runtime.getRuntime().addShutdownHook(new Thread("EventB TempDir Deleter") {
			@Override
			public void run() {
				ResourceGroovyMethods.deleteDir(tempdir);
			}
		});
		return tempdir;
	}

	private static class DummyElement extends AbstractElement {
		private String name;

		private DummyElement(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}
	}
}
