package de.prob.scripting;

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
import de.prob.animator.command.ICommand;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.classicalb.ClassicalBModel;

/**
 * Creates new {@link ClassicalBModel} objects.
 * 
 * @author joy
 * 
 */
public class ClassicalBFactory {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);
	private final Provider<ClassicalBModel> modelCreator;

	@Inject
	public ClassicalBFactory(final Provider<ClassicalBModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	/**
	 * @param f
	 * @return {@link ClassicalBModel} from the specified file
	 * @throws IOException
	 * @throws BException
	 */
	public ClassicalBModel load(final File f) throws IOException, BException {
		ClassicalBModel classicalBModel = modelCreator.get();
		BParser bparser = new BParser();
		Start ast = parseFile(f, bparser);
		final RecursiveMachineLoader rml = parseAllMachines(ast, f, bparser);

		classicalBModel.initialize(ast, rml);
		startAnimation(classicalBModel, rml);
		return classicalBModel;
	}

	private void startAnimation(final ClassicalBModel classicalBModel,
			final RecursiveMachineLoader rml) {
		final ICommand loadcmd = new LoadBProjectCommand(rml);
		classicalBModel.getStatespace().execute(loadcmd,
				new StartAnimationCommand());
		classicalBModel.getStatespace().setLoadcmd(loadcmd);
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

	/**
	 * @param model
	 * @param bparser
	 * @return AST after parsing model with bparser
	 * @throws IOException
	 * @throws BException
	 */
	public Start parseFile(final File model, final BParser bparser)
			throws IOException, BException {
		logger.trace("Parsing main file '{}'", model.getAbsolutePath());
		Start ast = null;
		ast = bparser.parseFile(model, false);
		return ast;
	}
}
