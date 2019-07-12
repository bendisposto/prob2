package de.prob.scripting;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.model.representation.XTLModel;

public class XTLFactory implements ModelFactory<XTLModel> {
	private final Provider<XTLModel> modelCreator;
	
	@Inject
	public XTLFactory(final Provider<XTLModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<XTLModel> extract(final String modelPath) throws IOException, ModelTranslationError {
		final File f = new File(modelPath);
		final XTLModel xtlModel = modelCreator.get().create(f);
		return new ExtractedModel<>(xtlModel, null);
	}
}
