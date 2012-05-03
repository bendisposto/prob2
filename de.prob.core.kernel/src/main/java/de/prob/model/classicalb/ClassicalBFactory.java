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
import de.prob.ProBException;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.notImplemented.StartAnimationCommand;

public class ClassicalBFactory {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);
	private final Provider<ClassicalBModel> modelCreator;

	@Inject
	public ClassicalBFactory(Provider<ClassicalBModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	public ClassicalBModel load(final File f) throws ProBException {
		ClassicalBModel classicalBModel = modelCreator.get();
		BParser bparser = new BParser();
		Start ast = parseFile(f, bparser);
		final RecursiveMachineLoader rml = parseAllMachines(
				ast, f, bparser);
		classicalBModel.initialize(ast, f);
		startAnimation(classicalBModel, rml);
		return classicalBModel;
	}

	private void startAnimation(ClassicalBModel classicalBModel,
			final RecursiveMachineLoader rml) throws ProBException {
		classicalBModel.getStatespace().execute(new LoadBProjectCommand(rml),
				new StartAnimationCommand());
	}

	public RecursiveMachineLoader parseAllMachines(final Start ast,
			final File f, final BParser bparser)
			throws ProBException {
		final RecursiveMachineLoader rml = new RecursiveMachineLoader(f.getParent(), bparser.getContentProvider() );
		try {
			rml.loadAllMachines(f, ast, null, bparser.getDefinitions(),
					bparser.getPragmas());
		} catch (BException e) {
			logger.error("Parser Error. {}.", e.getLocalizedMessage());
			logger.debug("Details", e);
			throw new ProBException();
		}
		logger.trace("Done parsing '{}'", f.getAbsolutePath());
		return rml;
	}

	public Start parseFile(final File model, BParser bparser) throws ProBException {
		logger.trace("Parsing main file '{}'", model.getAbsolutePath());
		Start ast = null;

		try {
			ast = bparser.parseFile(model, false);
		} catch (IOException e) {
			logger.error("IO Error {}.", e.getLocalizedMessage());
			logger.debug("Details", e);
			throw new ProBException();
		} catch (BException e) {
			logger.error("Parser Error. {}.", e.getLocalizedMessage());
			logger.debug("Details", e);
			throw new ProBException();
		}
		return ast;
	}
}
