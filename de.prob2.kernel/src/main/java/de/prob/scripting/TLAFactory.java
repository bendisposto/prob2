package de.prob.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.classicalb.ClassicalBModel;
import de.tla2b.exceptions.TLA2BException;
import de.tla2bAst.Translator;

public class TLAFactory extends ModelFactory<ClassicalBModel> {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);

	@Inject
	public TLAFactory(final Provider<ClassicalBModel> modelCreator) {
		super(modelCreator);
	}
	
	@Override
	public ExtractedModel<ClassicalBModel> extract(String fileName) throws IOException,
			ModelTranslationError {
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
			classicalBModel.initialize(ast, rml, f, bparser);
		} catch (BException e) {
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
