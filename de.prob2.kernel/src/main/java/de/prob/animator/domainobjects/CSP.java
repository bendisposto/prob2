package de.prob.animator.domainobjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import de.prob.Main;
import de.prob.animator.command.EvaluateFormulaCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.cli.OsInfoProvider;
import de.prob.cli.OsSpecificInfo;
import de.prob.model.representation.CSPModel;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.statespace.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Formula representation for CSP
 *
 * @author joy
 */
public class CSP extends AbstractEvalElement {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSP.class);

	private final FormulaUUID uuid;
	private final String fileName;
	private final String procname;

	/**
	 * When a new formula is entered, the entire model must be reparsed. For this reason,
	 * a {@link CSPModel} is one of the necessary parameters.
	 *
	 * @param formula string formula
	 * @param model csp model
	 */
	public CSP(String formula, CSPModel model) {
		super(formula);
		
		this.uuid = new FormulaUUID();
		this.fileName = model.getModelFile().getAbsolutePath();
		OsInfoProvider osInfoProvider = Main.getInjector().getInstance(OsInfoProvider.class);
		//TODO: die methode get( wird nicht erkannt
		OsSpecificInfo osInfo = osInfoProvider.get();
		String target = "";
		if (osInfo.getDirName().equals("win32")) {
			target = ".exe";
		}

		this.procname = Main.getProBDirectory() + "lib" + File.separator + "cspmf" + target;
	}

	@Override
	public void printProlog(IPrologTermOutput pout) {
		callCSPMF(pout, "translate", "--expressionToPrologTerm=" + this.getCode(), fileName);
	}

	public void printPrologAssertion(IPrologTermOutput pout) {
		callCSPMF(pout, "translate", "--declarationToPrologTerm=" + this.getCode(), fileName);
	}

	/* Calling the cspmf command:
	 * cspmf translate [OPTIONS] FILE
	 * where OPTIONS could be:
	 * --prologOut=FILE   translate a CSP-M file to Prolog
	 * --expressionToPrologTerm=STRING   translate a single CSP-M expression to Prolog
	 * --declarationToPrologTerm=STRING  translate a single CSP-M declaration to Prolog
	 * For more detailed description of all translating options just type
	 *  "cspmf translate --help" on the command line
	 */
	private void callCSPMF(final IPrologTermOutput pout, final String... args) {
		final List<String> cmd = new ArrayList<>();
		cmd.add(this.procname);
		cmd.addAll(Arrays.asList(args));
		try {
			final Process process = new ProcessBuilder(cmd).start();
			final int exitCode = process.waitFor();
			if (exitCode == 0) {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					pout.printString(reader.lines().collect(Collectors.joining("\n")));
				}
			} else {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					final String errorMessage = reader.lines().collect(Collectors.joining("\n"));
					throw new EvaluationException("Error parsing CSP " + errorMessage);
				}
			}
		} catch (IOException e) {
			LOGGER.error("IOException while calling cspmf", e);
			throw new EvaluationException("IOException while parsing CSP", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("Thread interrupted while calling cspmf", e);
			throw new EvaluationException("Thread interrupted while parsing CSP", e);
		}
	}

	@Override
	public EvalElementType getKind() {
		return EvalElementType.CSP;
	}

	@Override
	public String serialized() {
		Gson g = new Gson();
		return "#CSP:" + g.toJson(this);
	}

	@Override
	public IFormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(State stateId) {
		/* TODO: we could do a more efficient implementation here */
		return new EvaluateFormulaCommand(this, stateId.getId());
	}
}
