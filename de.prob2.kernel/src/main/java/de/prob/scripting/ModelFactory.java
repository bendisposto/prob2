package de.prob.scripting;

import java.io.IOException;

import de.prob.model.representation.AbstractModel;

public interface ModelFactory<T extends AbstractModel> {

	public ExtractedModel<T> extract(final String fileName) throws IOException,
	ModelTranslationError;
}
