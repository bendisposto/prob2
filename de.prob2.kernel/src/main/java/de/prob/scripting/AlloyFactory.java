package de.prob.scripting;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hhu.stups.alloy2b.translation.Alloy2BParser;
import de.hhu.stups.alloy2b.translation.Alloy2BParserErr;
import de.prob.exception.ProBError;
import de.prob.model.representation.AlloyModel;

public class AlloyFactory implements ModelFactory<AlloyModel> {
	private final Provider<AlloyModel> modelCreator;
	
	@Inject
	public AlloyFactory(final Provider<AlloyModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<AlloyModel> extract(final String modelPath) throws IOException, ModelTranslationError {
		final File f = new File(modelPath);
		final AlloyModel alloyModel;
		try {
			alloyModel = modelCreator.get().create(f, new Alloy2BParser().alloyToPrologTerm(f.getAbsolutePath()));
			return new ExtractedModel<>(alloyModel, null);
		} catch (final Alloy2BParserErr e) {
			throw new ProBError(e);
		}
	}
}
