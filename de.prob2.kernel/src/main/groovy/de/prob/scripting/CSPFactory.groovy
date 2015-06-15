package de.prob.scripting


import com.google.inject.Inject
import com.google.inject.Provider

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.LoadCSPCommand
import de.prob.animator.command.SetPreferenceCommand
import de.prob.animator.command.StartAnimationCommand
import de.prob.model.representation.CSPModel

class CSPFactory extends ModelFactory<CSPModel> {


	@Inject
	public CSPFactory(final Provider<CSPModel> modelCreator) {
		super(modelCreator)
	}
	
	@Override
	public ExtractedModel<CSPModel> extract(final String modelPath) throws IOException, ModelTranslationError {
		CSPModel cspModel = modelCreator.get()
		File f = new File(modelPath)
		cspModel.init(f.getText(),f)
		return new ExtractedModel<CSPModel>(cspModel, cspModel.getComponent(f.getName()));
	}
}
