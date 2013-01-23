

package de.prob.animator.domainobjects

import de.prob.Main
import de.prob.model.representation.AbstractElement
import de.prob.parser.ProBResultParser
import de.prob.parser.PrologTermGenerator
import de.prob.prolog.output.IPrologTermOutput
import de.prob.scripting.CSPModel

class CSP implements IEvalElement {

	private String code,home;
	private CSPModel model;

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
			if (it.startsWith("'bindval'")) s="yes("+it.substring(0,it.length()-1)+")"
		}

		def term = PrologTermGenerator.toPrologTerm(ProBResultParser.parse(s))
		pout.printTerm(term);
	}

	public String getKind() {
		return "csp";
	}
}
