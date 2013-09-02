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

import com.google.inject.Singleton;

import de.prob.web.WebUtils;

@Singleton
public class LtlModelCheck extends LtlPatternManager {

	public final String FORMULAS_FILE = "modelcheck/formulas.ltlf";
	private final String FORMULA_ID = "%% FORMULA";
	private final Logger logger = LoggerFactory.getLogger(LtlModelCheck.class);


	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/index.html");
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
