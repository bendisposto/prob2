package de.prob.scripting


import com.google.inject.Inject
import com.google.inject.Provider

import de.prob.model.representation.CSPModel

class CSPFactory implements ModelFactory<CSPModel> {

	def Provider<CSPModel> modelCreator

	@Inject
	public CSPFactory(final Provider<CSPModel> modelCreator) {
		this.modelCreator = modelCreator
	}

	@Override
	public ExtractedModel<CSPModel> extract(final String modelPath) throws IOException, ModelTranslationError {
		CSPModel cspModel = modelCreator.get()
		File f = new File(modelPath)
		cspModel = cspModel.create(f.getText(),f)
		return new ExtractedModel<CSPModel>(cspModel, cspModel.getComponent(f.getName()));
	}
}
