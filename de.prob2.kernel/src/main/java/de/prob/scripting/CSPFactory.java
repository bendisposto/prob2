package de.prob.scripting;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.prob.model.representation.CSPModel;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.File;
import java.io.IOException;

public class CSPFactory implements ModelFactory<CSPModel> {
	@Inject
	public CSPFactory(final Provider<CSPModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<CSPModel> extract(final String modelPath) throws IOException, ModelTranslationError {
		CSPModel cspModel = modelCreator.get();
		File f = new File(modelPath);
		cspModel = cspModel.create(ResourceGroovyMethods.getText(f), f);
		return new ExtractedModel<CSPModel>(cspModel, cspModel.getComponent(f.getName()));
	}

	public Provider<CSPModel> getModelCreator() {
		return modelCreator;
	}

	public void setModelCreator(Provider<CSPModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	private Provider<CSPModel> modelCreator;
}
