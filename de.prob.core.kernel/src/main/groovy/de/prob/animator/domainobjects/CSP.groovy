

package de.prob.animator.domainobjects

import de.prob.Main
import de.prob.model.representation.AbstractElement
import de.prob.parser.ProBResultParser
import de.prob.parser.PrologTermGenerator
import de.prob.prolog.output.IPrologTermOutput
import de.prob.scripting.CSPModel

class CSP implements IEvalElement {

	private String code,home;

	public CSP(String formula) {
		this.code = formula;
		this.home = Main.getProBDirectory();
	}

	public String getCode() {
		return code;
	}

	public void printProlog(IPrologTermOutput pout, AbstractElement m) {
		CSPModel model = m;
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
