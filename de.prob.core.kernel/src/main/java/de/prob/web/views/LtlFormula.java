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
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateId;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class LtlFormula extends AbstractSession implements
		IAnimationChangeListener {

	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);

	private final List<LTLFormulaTuple> formulas = new ArrayList<LTLFormulaTuple>();

	private final AnimationSelector animations;


	@Inject
	public LtlFormula(final AnimationSelector animations)
			throws LtlParseException {
		this.animations = animations;
		animations.registerAnimationChangeListener(this);

		LTL ltl1 = new LTL("GF[new]");
		LTL ltl2 = new LTL("F[new]");
		LTL ltl3 = new LTL("true");

		formulas.add(new LTLFormulaTuple(ltl1));
		formulas.add(new LTLFormulaTuple(ltl2));
		formulas.add(new LTLFormulaTuple(ltl3));
	}

	public void submitFormulas() {
		final int len = formulas.size();
		Object[] res = new Object[len];
		for (int i = 0; i < len; i++) {
			LTLFormulaTuple tuple = formulas.get(i);
			final String statusString = tuple.getStatus();
			res[i] = WebUtils.wrap("id", String.valueOf(i), "formulaText",
					tuple.getFormula().getCode(), "status", statusString);
		}
		Map<String, String> wrap = WebUtils.wrap("cmd",
				"LtlFormula.setFormulas", "formulas", WebUtils.toJson(res));

		submit(wrap);
	}

	public Object checkNthFormula(Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));
		LTLFormulaTuple tuple = formulas.get(pos);

		LTL formula = tuple.getFormula();

		// TODO: check formula in current state, show if outdated, etc.
		StateId stateid = animations.getCurrentTrace().getCurrentState();
		EvaluationCommand lcc = formula.getCommand(stateid);
		animations.getCurrentTrace().getStateSpace().execute(lcc);

		tuple.setStatus(lcc.getValues().get(0).getValue());

		submitFormulas();
		return null;
	}

	public Object removeFormula(Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));
		formulas.remove(pos);
		submitFormulas();
		return null;
	}

	public Object addFormula(Map<String, String[]> params)
			throws LtlParseException {
		String formula = get(params, "val");
		if (formula == null || formula.isEmpty()) {
			return null;
		}

		LTL ltl = new LTL(formula);

		formulas.add(new LTLFormulaTuple(ltl));
		logger.trace(params.toString());
		submitFormulas();
		return null;
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltlFormula/index.html");
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
		super.reload(client, lastinfo, context);
		submitFormulas();
	}

	@Override
	public void traceChange(Trace trace) {
		for (LTLFormulaTuple tuple : formulas) {
			tuple.resetStatus();
		}
		submitFormulas();
	}

	private class LTLFormulaTuple {

		private final LTL formula;
		private String status;

		public LTLFormulaTuple(LTL f) {
			formula = f;
			this.setStatus("unchecked");
		}

		public LTLFormulaTuple(LTL f, String status) {
			formula = f;
			this.setStatus(status);
		}

		public LTL getFormula() {
			return formula;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public void resetStatus() {
			status = "unchecked";
		}

	}

}
