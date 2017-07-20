package de.prob.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.classicalb.ClassicalBModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tla2b.exceptions.TLA2BException;
import de.tla2bAst.Translator;

public class TLAFactory implements ModelFactory<ClassicalBModel> {

	private static final Logger logger = LoggerFactory.getLogger(TLAFactory.class);
	private final Provider<ClassicalBModel> modelCreator;

	@Inject
	public TLAFactory(final Provider<ClassicalBModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<ClassicalBModel> extract(final String fileName) throws IOException, ModelTranslationError {
		ClassicalBModel classicalBModel = modelCreator.get();
		File f = new File(fileName);
		if (!f.exists()) {
			throw new FileNotFoundException("The TLA Model" + fileName + " was not found.");
		}

		Translator translator;
		Start ast;
		try {
			translator = new Translator(f.getAbsolutePath());
			ast = translator.translate();
		} catch (TLA2BException e) {
			throw new ModelTranslationError("Translation Error: " + e.getMessage(), e);
		}

		BParser bparser = new BParser();
		bparser.getDefinitions().addDefinitions(translator.getBDefinitions());
		try {
			final RecursiveMachineLoader rml = parseAllMachines(ast, f, bparser);
			classicalBModel = classicalBModel.create(ast, rml, f, bparser);
		} catch (BCompoundException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
		return new ExtractedModel<ClassicalBModel>(classicalBModel, classicalBModel.getMainMachine());
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
	 * @throws BCompoundException if the machines could not be loaded
	 */
	public RecursiveMachineLoader parseAllMachines(final Start ast, final File f, final BParser bparser)
			throws BCompoundException {
		final RecursiveMachineLoader rml = new RecursiveMachineLoader(f.getParent(), bparser.getContentProvider());

		rml.loadAllMachines(f, ast, null, bparser.getDefinitions());

		logger.trace("Done parsing '{}'", f.getAbsolutePath());
		return rml;
	}

	/**
	 * Parse a file into an AST {@link Start}.
	 * 
	 * @param model
	 *            {@link File} containing B machine
	 * @param bparser
	 *            {@link BParser} for parsing
	 * @return {@link Start} AST after parsing model with {@link BParser}
	 *         bparser
	 * @throws IOException if an I/O error occurred
	 * @throws BCompoundException if the file could not be parsed
	 */
	public Start parseFile(final File model, final BParser bparser) throws IOException, BCompoundException {
		logger.trace("Parsing main file '{}'", model.getAbsolutePath());
		Start ast = null;
		ast = bparser.parseFile(model, false);
		return ast;
	}

}
