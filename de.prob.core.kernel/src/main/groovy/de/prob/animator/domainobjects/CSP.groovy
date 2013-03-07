

package de.prob.animator.domainobjects

import de.prob.Main
import de.prob.parser.ProBResultParser
import de.prob.parser.PrologTermGenerator
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
	private CSPModel model;

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
		this.model = model;
	}

	public String getCode() {
		return code;
	}

	public void printProlog(IPrologTermOutput pout) {
		def nc = model.getContent()+"\n"+code;
		File tf = File.createTempFile("cspm", ".csp")
		tf << nc;
		def procname = home+"lib"+File.separator+"cspm"
		def fn = tf.getAbsolutePath()
		def process = (procname+" translate "+fn+" --prologOut="+fn+".cspm.pl").execute()
		process.waitFor()

		if (process.exitValue() != 0) {
			throw new Exception("Error parsing CSP "+process.err.text);
		}
		def s =""
		def c =  new File(fn+".cspm.pl").eachLine {
			if (it.startsWith("'bindval'")) s = it
		}
		pout.printString(s);
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
	public boolean equals(final Object that) {
		if (that instanceof CSP) {
			return ((CSP) that).getCode().equals(this.getCode());
		}
		return false;
	}
}
