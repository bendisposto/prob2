

package de.prob.animator.domainobjects

import com.google.gson.Gson

import de.prob.Main
import de.prob.animator.command.EvaluateFormulaCommand
import de.prob.animator.command.EvaluationCommand
import de.prob.cli.OsInfoProvider
import de.prob.model.representation.CSPModel
import de.prob.model.representation.FormulaUUID
import de.prob.prolog.output.IPrologTermOutput
import de.prob.statespace.State

/**
 * A Formula representation for CSP
 *
 * @author joy
 *
 */
class CSP extends AbstractEvalElement {

	private FormulaUUID uuid = new FormulaUUID();
	private String code,home;
	private String fileName;
	private OsInfoProvider osInfoProvider;
	private String target = ""
	private String procname;

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
		this.fileName = model.getModelFile().getAbsolutePath()
		def osInfoProvider = Main.getInjector().getInstance(OsInfoProvider.class);
		def osInfo = osInfoProvider.get()
		if(osInfo.dirName == "win32")
			this.target = ".exe"
		this.procname = home+"lib"+File.separator+"cspmf" + target
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
		def process = [
			this.procname,
			"translate",
			"--expressionToPrologTerm="+code,
			fileName
		].execute()
		executeCmd(process, pout)
	}

	// cspmf translate --declarationToPrologTerm="assert SKIP [T= STOP" "no-file"

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
		def process = [
			this.procname,
			"translate",
			"--declarationToPrologTerm="+code,
			fileName
		].execute()
		executeCmd(process, pout)

	}

	private void executeCmd(Process process, IPrologTermOutput pout) {
		process.waitFor()
		if (process.exitValue() != 0) {
			throw new EvaluationException("Error parsing CSP "+process.err.text);
		}
		pout.printString(process.getText());
	}

	/**
	 * @see de.prob.animator.domainobjects.IEvalElement#getKind()
	 *
	 * The kind for {@link CSP} formulas is "csp"
	 */
	public String getKind() {
		return "csp";
	}

	@Override
	public String toString() {
		return code;
	}

	@Override
	public String serialized() {
		Gson g = new Gson();
		return "#CSP:"+g.toJson(this);
	}

	@Override
	public FormulaUUID getFormulaId() {
		return uuid;
	}

	@Override
	public EvaluationCommand getCommand(State stateId) {
		/* TODO: we could do a more efficient implementation here */
		return new EvaluateFormulaCommand(this, stateId.getId());
	}
}
