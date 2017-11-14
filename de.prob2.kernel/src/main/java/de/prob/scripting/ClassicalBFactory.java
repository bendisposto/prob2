package de.prob.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.CachingDefinitionFileProvider;
import de.be4.classicalb.core.parser.IDefinitionFileProvider;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.classicalb.ClassicalBModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates new {@link ClassicalBModel} objects.
 *
 * @author joy
 *
 */
public class ClassicalBFactory implements ModelFactory<ClassicalBModel> {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);
	private final Provider<ClassicalBModel> modelCreator;

	@Inject
	public ClassicalBFactory(final Provider<ClassicalBModel> modelCreator) {
		this.modelCreator = modelCreator;
	}

	@Override
	public ExtractedModel<ClassicalBModel> extract(final String modelPath)
			throws IOException, ModelTranslationError {
		ClassicalBModel classicalBModel = modelCreator.get();

		File f = new File(modelPath);
		BParser bparser = new BParser();

		Start ast = parseFile(f, bparser);
		RecursiveMachineLoader rml = parseAllMachines(ast, f.getParent(), f,
				bparser.getContentProvider(), bparser);
		classicalBModel = classicalBModel.create(ast, rml, f, bparser);
		return new ExtractedModel<ClassicalBModel>(classicalBModel,
				classicalBModel.getMainMachine());
	}

	public ExtractedModel<ClassicalBModel> create(final String model)
			throws ModelTranslationError {
		ClassicalBModel classicalBModel = modelCreator.get();
		BParser bparser = new BParser();

		Start ast = parseString(model, bparser);
		final RecursiveMachineLoader rml = parseAllMachines(ast, ".", new File(
				""), bparser.getContentProvider(), bparser);
		classicalBModel = classicalBModel.create(ast, rml, new File("from_string"), bparser);
		return new ExtractedModel<ClassicalBModel>(classicalBModel,
				classicalBModel.getMainMachine());
	}

	public ExtractedModel<ClassicalBModel> create(final Start model)
			throws ModelTranslationError {
		ClassicalBModel classicalBModel = modelCreator.get();
		BParser bparser = new BParser();

		final RecursiveMachineLoader rml = parseAllMachines(model, ".",
				new File(""), new CachingDefinitionFileProvider(), bparser);
		classicalBModel = classicalBModel
				.create(model, rml, new File("from_string"), bparser);
		return new ExtractedModel<ClassicalBModel>(classicalBModel,
				classicalBModel.getMainMachine());
	}

	/**
	 * Given an {@link Start} ast, {@link File} f, and {@link BParser} bparser,
	 * all machines are loaded.
	 *
	 * @param ast
	 *            {@link Start} representing the abstract syntax tree for the
	 *            machine
	 * @param directory the directory relative to which machines should be loaded
	 * @param f
	 *            {@link File} containing machine
	 * @param contentProvider the content provider to use
	 * @param bparser
	 *            {@link BParser} for parsing
	 * @return {@link RecursiveMachineLoader} rml with all loaded machines
	 * @throws ModelTranslationError if the model could not be loaded
	 */
	public RecursiveMachineLoader parseAllMachines(final Start ast,
			final String directory, final File f,
			final IDefinitionFileProvider contentProvider, final BParser bparser)
					throws ModelTranslationError {
		try {
			final RecursiveMachineLoader rml = new RecursiveMachineLoader(
					directory, contentProvider);

			rml.loadAllMachines(f, ast, null, bparser.getDefinitions());
			logger.trace("Done parsing '{}'", f.getAbsolutePath());
			return rml;
		} catch (BCompoundException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
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
	 * @throws ModelTranslationError if the file could not be parsed
	 */
	public Start parseFile(final File model, final BParser bparser)
			throws IOException, ModelTranslationError {
		try {
			logger.trace("Parsing main file '{}'", model.getAbsolutePath());
			Start ast = null;
			ast = bparser.parseFile(model, false);
			return ast;
		} catch (BCompoundException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
	}

	private Start parseString(final String model, final BParser bparser)
			throws ModelTranslationError {
		try {
			logger.trace("Parsing file");
			Start ast = null;
			ast = bparser.parse(model, false);
			return ast;
		} catch (BCompoundException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
	}

}
