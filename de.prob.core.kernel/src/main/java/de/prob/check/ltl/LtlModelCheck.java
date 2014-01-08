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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.be4.classicalb.core.parser.ClassicalBParser;
import de.prob.animator.command.LtlCheckingCommand;
import de.prob.animator.command.LtlCheckingCommand.StartMode;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.LtlCheckingResult;
import de.prob.animator.domainobjects.LtlFormula;
import de.prob.ltl.parser.LtlParser;
import de.prob.parser.ResultParserException;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
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
		String mode = get(params, "startMode");
		String index = get(params, "index");
		String callback = get(params, "callbackObj");

		ParseListener listener = new ParseListener();
		LtlParser parser = parse(formula, listener);

		if (listener.getErrorMarkers().size() > 0) {
			// Parse error
			submit(checkFormulaError(1, index, callback));
		} else {
			try {
				if (checkFormula(formula, parser, mode)) {
					submit(WebUtils.wrap(
							"cmd", callback + ".checkFormulaPassed",
							"index", index));
				} else {
					submit(checkFormulaError(2, index, callback));
				}
			} catch (ResultParserException ex) {
				submit(checkFormulaError(3, index, callback));
			}
		}

		return WebUtils.wrap(
				"cmd", callback + ".checkFormulaFinished");
	}

	public Object checkFormulaList(Map<String, String[]> params) {
		logger.trace("Check formula list");

		String[] formulas = getArray(params, "formulas");
		String mode = get(params, "startMode");
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
				try {
					if (checkFormula(formula, parser, mode)) {
						submit(WebUtils.wrap(
								"cmd", callback + ".checkFormulaPassed",
								"index", index));
					} else {
						submit(checkFormulaError(2, index, callback));
					}
				} catch (ResultParserException ex) {
					submit(checkFormulaError(3, index, callback));
				}
			}
		}

		return WebUtils.wrap(
				"cmd", callback + ".checkFormulaListFinished");
	}

	private boolean checkFormula(String formula, LtlParser parser, String mode) {
		PrologTerm term = parser.generatePrologTerm("root", new ClassicalBParser());

		LtlFormula f = new LtlFormula(term);
		List<IEvalElement> formulaList = new LinkedList<IEvalElement>();
		formulaList.add(f);

		StartMode startMode = StartMode.init;
		if (mode.equals("starthere")) {
			startMode = StartMode.starthere;
		} else if (mode.equals("checkhere")) {
			startMode = StartMode.checkhere;
		}

		LtlCheckingResult result =  LtlCheckingCommand.modelCheck(currentStateSpace,
				formulaList,
				500, startMode,
				new StateId("root", currentStateSpace));
		return (result.getCounterexample() == null);
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
