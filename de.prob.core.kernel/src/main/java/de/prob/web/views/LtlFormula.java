package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class LtlFormula extends AbstractSession {

	enum status {
		unchecked, f, t;
	}

	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);

	// private final Map<PrologTerm, status> formulas = new HashMap<PrologTerm,
	// status>();
	private final List<PrologTerm> formulas = new ArrayList<PrologTerm>();
	private final List<status> stati = new ArrayList<status>();

	public LtlFormula() {
		CompoundPrologTerm cpt1 = new CompoundPrologTerm("globally",
				new CompoundPrologTerm("finally", new CompoundPrologTerm(
						"action", new CompoundPrologTerm("btrans",
								new CompoundPrologTerm("event",
										new CompoundPrologTerm("new"))))));
		CompoundPrologTerm cpt2 = new CompoundPrologTerm("finally",
				new CompoundPrologTerm("action", new CompoundPrologTerm(
						"btrans", new CompoundPrologTerm("event",
								new CompoundPrologTerm("new")))));
		CompoundPrologTerm cpt3 = new CompoundPrologTerm("true");
		formulas.add(cpt1);
		formulas.add(cpt2);
		formulas.add(cpt3);
		stati.add(status.unchecked);
		stati.add(status.unchecked);
		stati.add(status.unchecked);
	}

	public void submit_formulas() {
		String[] stringRepresentation = new String[formulas.size()];
		for (int i = 0; i < formulas.size(); i++) {
			stringRepresentation[i] = formulas.get(i).toString();
		}
		// Object[] formulaArray = formulas.toArray();
		// Object[] formulaArray = stringRepresentation.toArray();
		Object[] statiArray = stati.toArray();
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"LtlFormula.setFormulas",
				"formulas",
				WebUtils.toJson(stringRepresentation), "stati",
				WebUtils.toJson(statiArray));
		submit(wrap);
	}

	public Object gotoPos(Map<String, String[]> params) {
		logger.trace("Goto Position in LtlFormula");
		int pos = Integer.parseInt(get(params, "pos"));
		stati.remove(pos);
		stati.add(pos, status.t);
		submit_formulas();
		return null;
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltlFormula/index.html");
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
		super.reload(client, lastinfo, context);
		submit_formulas();
	}

}
