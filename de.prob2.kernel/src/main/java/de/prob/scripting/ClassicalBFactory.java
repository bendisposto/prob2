package de.prob.scripting;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.CachingDefinitionFileProvider;
import de.be4.classicalb.core.parser.IDefinitionFileProvider;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.classicalb.ClassicalBModel;

/**
 * Creates new {@link ClassicalBModel} objects.
 *
 * @author joy
 *
 */
public class ClassicalBFactory extends ModelFactory<ClassicalBModel> {

	Logger logger = LoggerFactory.getLogger(ClassicalBFactory.class);

	@Inject
	public ClassicalBFactory(final Provider<ClassicalBModel> modelCreator) {
		super(modelCreator);
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
		classicalBModel.initialize(ast, rml, f, bparser);
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
		classicalBModel.initialize(ast, rml, new File("from_string"), bparser);
		return new ExtractedModel<ClassicalBModel>(classicalBModel,
				classicalBModel.getMainMachine());
	}

	public ExtractedModel<ClassicalBModel> create(final Start model)
			throws ModelTranslationError {
		ClassicalBModel classicalBModel = modelCreator.get();
		BParser bparser = new BParser();

		final RecursiveMachineLoader rml = parseAllMachines(model, ".",
				new File(""), new CachingDefinitionFileProvider(), bparser);
		classicalBModel
		.initialize(model, rml, new File("from_string"), bparser);
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
	 * @param f
	 *            {@link File} containing machine
	 * @param bparser
	 *            {@link BParser} for parsing
	 * @return {@link RecursiveMachineLoader} rml with all loaded machines
	 * @throws BException
	 */
	public RecursiveMachineLoader parseAllMachines(final Start ast,
			final String directory, final File f,
			final IDefinitionFileProvider contentProvider, final BParser bparser)
					throws ModelTranslationError {
		try {
			final RecursiveMachineLoader rml = new RecursiveMachineLoader(
					directory, contentProvider);

			rml.loadAllMachines(f, ast, null, bparser.getDefinitions(),
					bparser.getPragmas());
			logger.trace("Done parsing '{}'", f.getAbsolutePath());
			return rml;
		} catch (BException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
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
			throws IOException, ModelTranslationError {
		try {
			logger.trace("Parsing main file '{}'", model.getAbsolutePath());
			Start ast = null;
			ast = bparser.parseFile(model, false);
			return ast;
		} catch (BException e) {
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
		} catch (BException e) {
			throw new ModelTranslationError(e.getMessage(), e);
		}
	}

}
