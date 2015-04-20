package de.prob.web.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.ltl.core.parser.LtlParseException;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.domainobjects.LTL;
import de.prob.annotations.OneToOne;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.State;
import de.prob.statespace.Trace;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

@OneToOne
public class LtlFormula extends AbstractAnimationBasedView {

	private final Logger logger = LoggerFactory.getLogger(CurrentTrace.class);

	private final List<LTLFormulaTuple> formulas = new ArrayList<LTLFormulaTuple>();

	private final Map<LTL, Map<State, String>> cache = new HashMap<LTL, Map<State, String>>();

	@Inject
	public LtlFormula(final AnimationSelector animations)
			throws LtlParseException {
		super(animations);
		incrementalUpdate = false;
		animations.registerAnimationChangeListener(this);
		addFormulas();
	}

	private void addFormulas() throws LtlParseException {
		LTL ltl1 = new LTL("GF[new]");
		LTL ltl2 = new LTL("F[new]");
		LTL ltl3 = new LTL("true");

		formulas.add(new LTLFormulaTuple(ltl1));
		formulas.add(new LTLFormulaTuple(ltl2));
		formulas.add(new LTLFormulaTuple(ltl3));
		cache.put(ltl1, new HashMap<State, String>());
		cache.put(ltl2, new HashMap<State, String>());
		cache.put(ltl3, new HashMap<State, String>());
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

	public Object checkNthFormula(final Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));
		LTLFormulaTuple tuple = formulas.get(pos);

		LTL formula = tuple.getFormula();

		Trace currentTrace = getCurrentTrace();
		if (currentTrace != null) {
			State stateid = currentTrace.getCurrentState();
			EvaluationCommand lcc = formula.getCommand(stateid);
			currentTrace.getStateSpace().execute(lcc);

			String result = lcc.getValue().toString();
			tuple.setStatus(result);
			cache.get(formula).put(stateid, result);

			submitFormulas();
		}
		return null;
	}

	public Object removeFormula(final Map<String, String[]> params) {
		int pos = Integer.parseInt(get(params, "pos"));
		formulas.remove(pos);
		submitFormulas();
		return null;
	}

	public Object addFormula(final Map<String, String[]> params)
			throws LtlParseException {
		String formula = get(params, "val");
		if (formula == null || formula.isEmpty()) {
			return null;
		}

		LTL ltl = new LTL(formula);

		formulas.add(new LTLFormulaTuple(ltl));
		cache.put(ltl, new HashMap<State, String>());
		logger.trace(params.toString());
		submitFormulas();
		return null;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltlFormula/index.html");
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		submitFormulas();
	}

	@Override
	public void performTraceChange(final Trace trace) {
		if (trace != null) {
			State current = trace.getCurrentState();
			for (LTLFormulaTuple tuple : formulas) {
				String cached = cache.get(tuple.formula).get(current);
				if (cached != null) {
					tuple.setStatus(cached);
				} else {
					tuple.resetStatus();
				}

			}
			submitFormulas();
		}
	}

	private class LTLFormulaTuple {

		private final LTL formula;
		private String status;

		public LTLFormulaTuple(final LTL f) {
			formula = f;
			setStatus("unchecked");
		}

		public LTL getFormula() {
			return formula;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(final String status) {
			this.status = status;
		}

		public void resetStatus() {
			status = "unchecked";
		}

	}

	@Override
	public void animatorStatus(final boolean busy) {
		if (busy) {
			submit(WebUtils.wrap("cmd", "LtlFormula.disable"));
		} else {
			submit(WebUtils.wrap("cmd", "LtlFormula.enable"));
		}
	}

}
