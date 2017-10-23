package de.prob.model.brules;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.rules.RulesProject;

import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.scripting.ExtractedModel;

public class RulesModelFactory {

	private final Provider<RulesModel> modelCreator;

	@Inject
	public RulesModelFactory(final Provider<RulesModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	public ExtractedModel<RulesModel> extract(File runnerFile, RulesProject rulesProject) {
		RulesModel rulesModel = modelCreator.get();

		rulesModel = rulesModel.create(runnerFile, rulesProject);
		return new ExtractedModel<>(rulesModel,
				new ClassicalBMachine(rulesProject.getBModels().get(0).getMachineName()));
	}
}
