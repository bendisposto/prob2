package de.prob.scripting;

import groovy.lang.Closure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.classicalb.ClassicalBModel;
import de.tla2b.exceptions.FrontEndException;
import de.tla2b.exceptions.TLA2BException;
import de.tla2bAst.Translator;

public class TLAFactory extends ModelFactory {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);
	private final Provider<ClassicalBModel> modelCreator;

	@Inject
	public TLAFactory(final Provider<ClassicalBModel> modelCreator,
			final FileHandler fileHandler) {
		super(fileHandler);
		this.modelCreator = modelCreator;
	}

	/**
	 * This method loads a TLA module from file, parses the module, translates
	 * the TLA AST to a B AST, starts the animation, and returns the created
	 * {@link ClassicalBModel}
	 * 
	 * @param f
	 *            {@link File} containing the TLA module to be loaded.
	 * @param prefs
	 * @return {@link ClassicalBModel} translated from the specified TLA file.
	 * @throws IOException
	 * @throws BException
	 * @throws FrontEndException
	 */
	public ClassicalBModel load(final File f, final Map<String, String> prefs,
			final Closure<?> loader) throws IOException, BException {
		ClassicalBModel classicalBModel = modelCreator.get();

		Translator translator;
		Start ast;
		try {
			translator = new Translator(f.getAbsolutePath());
			ast = translator.translate();
		} catch (TLA2BException e) {
			e.printStackTrace();
			throw new RuntimeException("Translation error");
		}

		BParser bparser = new BParser();
		bparser.getDefinitions().addAll(translator.getBDefinitions());

		final RecursiveMachineLoader rml = parseAllMachines(ast, f, bparser);
		classicalBModel.initialize(ast, rml, f);
		startAnimation(classicalBModel, rml,
				getPreferences(classicalBModel, prefs), f);
		loader.call(classicalBModel);
		return classicalBModel;

	}

	/**
	 * Starts animation in the Prolog kernel with the given
	 * {@link ClassicalBModel} classicalBModel and
	 * {@link RecursiveMachineLoader} rml
	 * 
	 * @param classicalBModel
	 *            {@link ClassicalBModel} representing the loaded model
	 * @param rml
	 *            {@link RecursiveMachineLoader} containing all of the parsed
	 *            machines
	 * @param prefs
	 * @param f
	 */
	private void startAnimation(final ClassicalBModel classicalBModel,
			final RecursiveMachineLoader rml, final Map<String, String> prefs,
			final File f) {

		List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

		for (Entry<String, String> pref : prefs.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		final AbstractCommand loadcmd = new LoadBProjectCommand(rml, f);
		cmds.add(loadcmd);
		cmds.add(new StartAnimationCommand());
		classicalBModel.getStateSpace().execute(new ComposedCommand(cmds));
		classicalBModel.getStateSpace().setLoadcmd(loadcmd);
	}

	/**
	 * Given an {@link Start} ast, {@link File} f, and {@link BParser} bparser,
	 * all machines are loaded.
	 * 
	 * @param ast
	 *            {@link Start} representing the abstract syntax tree for the
	 *            machine
	 * @param f
	 *            {@link File} containing machine
	 * @param bparser
	 *            {@link BParser} for parsing
	 * @return {@link RecursiveMachineLoader} rml with all loaded machines
	 * @throws BException
	 */
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
	 *            {@link File} containing B machine
	 * @param bparser
	 *            {@link BParser} for parsing
	 * @return {@link Start} AST after parsing model with {@link BParser}
	 *         bparser
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
