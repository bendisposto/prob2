package de.prob.model.classicalb;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.notImplemented.StartAnimationCommand;

public class ClassicalBFactory {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);
	private final Provider<ClassicalBModel> modelCreator;

	@Inject
	public ClassicalBFactory(Provider<ClassicalBModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	public ClassicalBModel load(final File f) throws IOException, BException {
		ClassicalBModel classicalBModel = modelCreator.get();
		BParser bparser = new BParser();
		Start ast = parseFile(f, bparser);
		final RecursiveMachineLoader rml = parseAllMachines(ast, f, bparser);

		classicalBModel.initialize(ast, rml);
		startAnimation(classicalBModel, rml);
		return classicalBModel;
	}

	private void startAnimation(ClassicalBModel classicalBModel,
			final RecursiveMachineLoader rml) {
		classicalBModel.getStatespace().execute(new LoadBProjectCommand(rml),
				new StartAnimationCommand());
	}

	public RecursiveMachineLoader parseAllMachines(final Start ast,
			final File f, final BParser bparser) throws BException {
		final RecursiveMachineLoader rml = new RecursiveMachineLoader(
				f.getParent(), bparser.getContentProvider());

		rml.loadAllMachines(f, ast, null, bparser.getDefinitions(),
				bparser.getPragmas());

		logger.trace("Done parsing '{}'", f.getAbsolutePath());
		return rml;
	}

	public Start parseFile(final File model, BParser bparser)
			throws IOException, BException {
		logger.trace("Parsing main file '{}'", model.getAbsolutePath());
		Start ast = null;
		ast = bparser.parseFile(model, false);
		return ast;
	}
}
