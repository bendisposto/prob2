package de.prob.animator.domainobjects;

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
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ProcessGroovyMethods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Formula representation for CSP
 *
 * @author joy
 */
public class CSP extends AbstractEvalElement {
	/**
	 * When a new formula is entered, the entire model must be reparsed. For this reason,
	 * a {@link CSPModel} is one of the necessary parameters.
	 *
	 * @param formula
	 * @param model
	 */
	public CSP(String formula, CSPModel model) {
		this.code = formula;
		this.home = Main.getProBDirectory();
		this.fileName = model.getModelFile().getAbsolutePath();
		OsInfoProvider osInfoProvider = Main.getInjector().getInstance(OsInfoProvider.class);
		//TODO: die methode get( wird nicht erkannt
		OsSpecificInfo osInfo = osInfoProvider.get();
		if (osInfo.getDirName().equals("win32")) {
			this.target = ".exe";
		}

		this.procname = home + "lib" + File.separator + "cspmf" + target;
		this.expansion = FormulaExpand.TRUNCATE;// this doesn't matter
	}

	public String getCode() {
		return code;
	}

	public void printProlog(IPrologTermOutput pout) {

		/* Calling the cspmf command:
         * cspmf translate [OPTIONS] FILE
		 * where OPTIONS could be:
		 --prologOut=FILE   translate a CSP-M file to Prolog
		 --expressionToPrologTerm=STRING   translate a single CSP-M expression to Prolog
		 --declarationToPrologTerm=STRING  translate a single CSP-M declaration to Prolog
		 * For more detailed description of all translating options just type
		 *  "cspmf translate --help" on the command line
		 */
		try {
			Process process = ProcessGroovyMethods.execute(new ArrayList<String>(Arrays.asList(this.procname, "translate", "--expressionToPrologTerm=" + code, fileName)));
			executeCmd(process, pout);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void printPrologAssertion(IPrologTermOutput pout) {

		/* Calling the cspmf command:
		 * cspmf translate [OPTIONS] FILE
		 * where OPTIONS could be:
		 --prologOut=FILE   translate a CSP-M file to Prolog
		 --expressionToPrologTerm=STRING   translate a single CSP-M expression to Prolog
		 --declarationToPrologTerm=STRING  translate a single CSP-M declaration to Prolog
		 * For more detailed description of all translating options just type
		 *  "cspmf translate --help" on the command line
		 */
		try {
			Process process = ProcessGroovyMethods.execute(new ArrayList<String>(Arrays.asList(this.procname, "translate", "--declarationToPrologTerm=" + code, fileName)));
			executeCmd(process, pout);
		} catch (IOException e){
			e.printStackTrace();
		}

	}

	private boolean executeCmd(Process process) {
		try {
			process.waitFor();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		return process.exitValue() == 0;
	}

	private void executeCmd(Process process, IPrologTermOutput pout) {
		if (executeCmd(process)) {
			try {
				pout.printString(ProcessGroovyMethods.getText(process));
			} catch (IOException e){
				e.printStackTrace();
			}
		} else {
			try {
				throw new EvaluationException("Error parsing CSP " + IOGroovyMethods.getText(ProcessGroovyMethods.getErr(process)));
			} catch (IOException e){
				e.printStackTrace();
			}
		}

	}

	@Override
	public EvalElementType getKind() {
		return EvalElementType.CSP;
	}

	@Override
	public String toString() {
		return code;
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

	private FormulaUUID uuid = new FormulaUUID();
	private String code;
	private String home;
	private String fileName;
	private Object osInfoProvider;
	private String target = "";
	private String procname;
}
