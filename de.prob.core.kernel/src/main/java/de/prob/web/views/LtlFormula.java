package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.ClassicalBParser;
import de.be4.ltl.core.parser.LtlParseException;
import de.be4.ltl.core.parser.LtlParser;
import de.prob.animator.command.LtlCheckingCommand;
import de.prob.animator.command.LtlCheckingCommand.StartMode;
import de.prob.animator.command.LtlCheckingCommand.Status;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.AnimationSelector;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class LtlFormula extends AbstractSession {

	enum status {
		unchecked, FALSE, TRUE;
	}

	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);

	private final List<PrologTerm> formulas = new ArrayList<PrologTerm>();
	private final List<status> stati = new ArrayList<status>();

	private final AnimationSelector animations;
	private final LtlParser ltlParser;

	@Inject
	public LtlFormula(final AnimationSelector animations) {
		this.animations = animations;
		/* TODO: worry about the language specific parser */
		ltlParser = new LtlParser(new ClassicalBParser());

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
		final int len = formulas.size();
		Object[] res = new Object[len];
		for (int i = 0; i < len; i++) {
			res[i] = WebUtils.wrap("id", String.valueOf(i), "formulaText",
					formulas.get(i), "status", stati.get(i)
							.toString());
		}
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"LtlFormula.setFormulas", "formulas", WebUtils.toJson(res));

		submit(wrap);
	}

	public Object checkNthFormula(Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));
		if (stati.get(pos) != status.unchecked) {
			return null;
		}

		PrologTerm formula = formulas.get(pos);
		LtlCheckingCommand lcc = new LtlCheckingCommand(formula, 500,
				StartMode.init);
		animations.getCurrentTrace().getStateSpace().execute(lcc);

		stati.remove(pos);
		if (lcc.getResult().getStatus() == Status.counterexample) {
			stati.add(pos, status.FALSE);
		} else {
			stati.add(pos, status.TRUE);
		}

		submit_formulas();
		return null;
	}

	public Object removeFormula(Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));
		formulas.remove(pos);
		stati.remove(pos);
		submit_formulas();
		return null;
	}

	public Object addFormula(Map<String, String[]> params)
			throws LtlParseException {
		String formula = get(params, "val");
		if (formula == null || formula.isEmpty()) {
			return null;
		}


		PrologTerm compoundPrologTerm = ltlParser.generatePrologTerm(
				formula, "root");
		formulas.add(compoundPrologTerm);
		stati.add(status.unchecked);
		logger.trace(params.toString());
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
