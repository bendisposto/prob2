package de.prob.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.model.representation.CSPModel;

public class CSPFactory implements ModelFactory<CSPModel> {
	private final Provider<CSPModel> modelCreator;
	
	@Inject
	public CSPFactory(final Provider<CSPModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<CSPModel> extract(final String modelPath) throws IOException, ModelTranslationError {
		CSPModel cspModel = modelCreator.get();
		File f = new File(modelPath);
		final String text;
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
			text = reader.lines().collect(Collectors.joining("\n"));
		}
		cspModel = cspModel.create(text, f);
		return new ExtractedModel<>(cspModel, cspModel.getComponent(f.getName()));
	}
}
