package de.prob.scripting;

import groovy.lang.Closure;

import java.io.File;
import java.io.FileNotFoundException;
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
import de.tla2b.exceptions.TLA2BException;
import de.tla2bAst.Translator;

public class TLAFactory extends ModelFactory<ClassicalBModel> {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);

	@Inject
	public TLAFactory(final Provider<ClassicalBModel> modelCreator,
			final FileHandler fileHandler) {
		super(modelCreator, fileHandler, LoadClosures.getB());
	}

	@Override
	public ClassicalBModel load(final String fileName,
			final Map<String, String> preferences, final Closure<Object> loader)
			throws IOException, ModelTranslationError {
		ClassicalBModel classicalBModel = modelCreator.get();
		File f = new File(fileName);
		if (!f.exists()) {
			throw new FileNotFoundException("The TLA Model" + fileName
					+ " was not found.");
		}

		Translator translator;
		Start ast;
		try {
			translator = new Translator(f.getAbsolutePath());
			ast = translator.translate();
		} catch (TLA2BException e) {
			throw new ModelTranslationError("Translation Error: "
					+ e.getMessage(), e);
		}

		BParser bparser = new BParser();
		bparser.getDefinitions().addAll(translator.getBDefinitions());
		try {
			final RecursiveMachineLoader rml = parseAllMachines(ast, f, bparser);
			classicalBModel.initialize(ast, rml, f);
			startAnimation(classicalBModel, rml,
					getPreferences(classicalBModel, preferences), f);
			loader.call(classicalBModel);
		} catch (BException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
		return classicalBModel;

	}

	/**
	 * This method is deprecated. Use
	 * {@link TLAFactory#load(String, Map, Closure)} instead.
	 * 
	 * @param f
	 *            {@link File} to be loaded
	 * @param prefs
	 *            preferences for the loading process
	 * @param loader
	 *            actions to take place after the loading process
	 * @return {@link ClassicalBModel} translated from the specified TLA file.
	 * @throws IOException
	 * @throws ModelTranslationError
	 */
	@Deprecated
	public ClassicalBModel load(final File f, final Map<String, String> prefs,
			final Closure<Object> loader) throws IOException,
			ModelTranslationError {
		return load(f.getAbsolutePath(), prefs, loader);
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
