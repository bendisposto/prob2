package de.prob.scripting;

import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.model.representation.AbstractModel;

public abstract class ModelFactory<T extends AbstractModel> {

	/**
	 * Name of file in which the preferences are saved. Currently
	 * "prob2preferences"
	 */
	public final String PREFERENCE_FILE_NAME = "prob2preferences";
	protected final Provider<T> modelCreator;

	@Inject
	public ModelFactory(final Provider<T> modelCreator) {
		this.modelCreator = modelCreator;
	}

	public abstract ExtractedModel<T> extract(final String fileName) throws IOException,
			ModelTranslationError;
}
