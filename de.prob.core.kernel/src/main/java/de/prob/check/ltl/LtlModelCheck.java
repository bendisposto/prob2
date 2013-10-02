package de.prob.check.ltl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.command.LtlCheckingCommand;
import de.prob.animator.command.LtlCheckingCommand.StartMode;
import de.prob.ltl.parser.LtlParser;
import de.prob.parserbase.UnparsedParserBase;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.web.WebUtils;

@Singleton
public class LtlModelCheck extends LtlPatternManager {

	public final String FORMULAS_FILE = "D://modelcheck/formulas.ltlf";
	private final String FORMULA_ID = "%% FORMULA";
	private final Logger logger = LoggerFactory.getLogger(LtlModelCheck.class);

	private final StateSpace currentStateSpace;

	@Inject
	public LtlModelCheck(final AnimationSelector animations) {
		Trace currentTrace = animations.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening Value over Time visualization");
		}
		currentStateSpace = currentTrace.getStateSpace();
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/index.html");
	}

	public Object checkFormula(Map<String, String[]> params) {
		logger.trace("Check formula");

		String formula = get(params, "formula");
		String index = get(params, "index");
		String callback = get(params, "callbackObj");

		ParseListener listener = new ParseListener();
		LtlParser parser = parse(formula, listener);

		if (listener.getErrorMarkers().size() > 0) {
			// Parse error
			return checkFormulaError(1, index, callback);
		} else {
			PrologTerm term = parser.generatePrologTerm("root", new UnparsedParserBase("", "", ""));

			LtlCheckingCommand command = new LtlCheckingCommand(term, 500, StartMode.init);
			currentStateSpace.execute(command);

			//command.getResult();
			// TODO remove random
			if (new Random().nextBoolean()) {
				return WebUtils.wrap(
						"cmd", callback + ".checkFormulaPassed",
						"index", index);
			} else {
				return checkFormulaError(2, index, callback);
			}
		}
	}

	public Object checkFormulaList(Map<String, String[]> params) {
		logger.trace("Check formula list");

		String[] formulas = getArray(params, "formulas");
		String[] indizes = getArray(params, "indizes");
		String callback = get(params, "callbackObj");

		for (int i = 0; i < formulas.length; i++) {
			String formula = formulas[i];
			String index = indizes[i];
			ParseListener listener = new ParseListener();
			LtlParser parser = parse(formula, listener);

			if (listener.getErrorMarkers().size() > 0) {
				// Parse error
				submit(checkFormulaError(1, index, callback));
			} else {
				PrologTerm term = parser.generatePrologTerm("root", new UnparsedParserBase("", "", ""));

				LtlCheckingCommand command = new LtlCheckingCommand(term, 500, StartMode.init);
				//currentStateSpace.execute(command);

				//command.getResult();
				// TODO remove random
				if (new Random().nextBoolean()) {
					submit(WebUtils.wrap(
							"cmd", callback + ".checkFormulaPassed",
							"index", index));
				} else {
					submit(checkFormulaError(2, index, callback));
				}
			}
		}

		return WebUtils.wrap(
				"cmd", callback + ".checkFormulaListFinished");
	}

	private Object checkFormulaError(int error, String index, String callback) {
		return WebUtils.wrap(
				"cmd", callback + ".checkFormulaFailed",
				"index", index,
				"error", error + "");
	}

	public Object getFormulaList(Map<String, String[]> params) {
		logger.trace("Get formula list");

		String callback = get(params, "callbackObj");

		List<String> formulas = null;
		try {
			formulas = loadFormulas(FORMULAS_FILE);
		} catch (IOException e) {
		}

		return WebUtils.wrap(
				"cmd", callback + ".setFormulaList",
				"formulas", (formulas != null ? WebUtils.toJson(formulas) : ""));
	}

	public Object saveFormulaList(Map<String, String[]> params) {
		logger.trace("Save formula list");

		String formulas[] = getArray(params, "formulas");
		String callback = get(params, "callbackObj");

		try {
			saveFormulas(FORMULAS_FILE, formulas);
		} catch (IOException e) {
		}

		return WebUtils.wrap(
				"cmd", callback + ".saveFormulaListSuccess");
	}

	private List<String> loadFormulas(String filename) throws IOException {
		List<String> formulas = new LinkedList<String>();

		BufferedReader reader = null;
		try {
			InputStream stream = getClass().getResourceAsStream(filename);
			if (stream == null) {
				stream = new FileInputStream(filename);
			}
			if (stream != null) {
				reader = new BufferedReader(new InputStreamReader(stream));

				String line = null;
				StringBuilder formulaBuilder = null;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(FORMULA_ID)) {
						if (formulaBuilder != null) {
							formulas.add(formulaBuilder.toString());
						}
						formulaBuilder = new StringBuilder();
					} else {
						if (formulaBuilder.length() > 0) {
							formulaBuilder.append('\n');
						}
						formulaBuilder.append(line);
					}
				}
				if (formulaBuilder != null && formulaBuilder.length() > 0) {
					formulas.add(formulaBuilder.toString());
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return formulas;
	}

	private void saveFormulas(String filename, String[] formulas) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(filename)));
			for (String formula : formulas) {
				writer.write(FORMULA_ID);
				writer.newLine();
				writer.write(formula);
				writer.newLine();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
