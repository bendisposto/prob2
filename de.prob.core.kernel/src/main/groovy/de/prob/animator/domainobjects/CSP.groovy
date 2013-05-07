

package de.prob.animator.domainobjects

import com.google.gson.Gson

import de.prob.Main
import de.prob.prolog.output.IPrologTermOutput
import de.prob.scripting.CSPModel

/**
 * A Formula representation for CSP
 *
 * @author joy
 *
 */
class CSP implements IEvalElement {


	private String code,home;
	private String fileName;

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
	}

	public String getCode() {
		return code;
	}

	public void printProlog(IPrologTermOutput pout) {
		def procname = home+"lib"+File.separator+"cspmf"
		/* Calling the cspmf command:
		 * cspmf translate [OPTIONS] FILE
		 * where OPTIONS could be:
		    --prologOut=FILE   translate a CSP-M file to Prolog
            --expressionToPrologTerm=STRING   translate a single CSP-M expression to Prolog
            --declarationToPrologTerm=STRING  translate a single CSP-M declaration to Prolog
		 * For more detailed description of all translating options just type
		 *  "cspmf translate --help" on the command line
		 */
		def process = (procname+" translate "+" --expressionToPrologTerm="+code+" "+fileName).execute()
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
}
