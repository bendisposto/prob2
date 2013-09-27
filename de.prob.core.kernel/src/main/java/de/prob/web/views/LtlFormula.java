package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.ltl.core.parser.LtlParseException;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.domainobjects.LTL;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class LtlFormula extends AbstractSession {

	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);

	private final List<LTL> formulas = new ArrayList<LTL>();
	private final List<String> stati = new ArrayList<String>();

	private final AnimationSelector animations;


	@Inject
	public LtlFormula(final AnimationSelector animations)
			throws LtlParseException {
		this.animations = animations;

		LTL ltl1 = new LTL("GF[new]");
		LTL ltl2 = new LTL("F[new]");
		LTL ltl3 = new LTL("true");

		formulas.add(ltl1);
		formulas.add(ltl2);
		formulas.add(ltl3);
		stati.add("unchecked");
		stati.add("unchecked");
		stati.add("unchecked");
	}

	public void submit_formulas() {
		final int len = formulas.size();
		Object[] res = new Object[len];
		for (int i = 0; i < len; i++) {
			final String statusString = stati.get(i);
			res[i] = WebUtils.wrap("id", String.valueOf(i), "formulaText",
					formulas.get(i).getCode(), "status", statusString);
		}
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"LtlFormula.setFormulas", "formulas", WebUtils.toJson(res));

		submit(wrap);
	}

	public Object checkNthFormula(Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));


		LTL formula = formulas.get(pos);

		// TODO: check formula in current state, show if outdated, etc.
		StateId stateid = animations.getCurrentTrace().getCurrentState();
		EvaluationCommand lcc = formula.getCommand(stateid);
		animations.getCurrentTrace().getStateSpace().execute(lcc);

		stati.remove(pos);
		stati.add(pos, lcc.getValues().get(0).getValue());

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

		LTL ltl = new LTL(formula);

		formulas.add(ltl);
		stati.add("unchecked");
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
