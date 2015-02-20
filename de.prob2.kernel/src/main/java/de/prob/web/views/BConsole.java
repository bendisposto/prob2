package de.prob.web.views;

import java.util.Map;
import java.util.UUID;

import javax.servlet.AsyncContext;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.LoadBProjectFromStringCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.FormalismType;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class BConsole extends AbstractSession implements
		IAnimationChangeListener {

	private final StateSpace defaultSS;
	private String modelName;
	private Trace currentTrace;

	@Inject
	public BConsole(final UUID id, final Provider<StateSpace> ssProvider,
			final AnimationSelector animations) {
		super(id);
		defaultSS = ssProvider.get();
		try {
			LoadBProjectFromStringCommand cmd = new LoadBProjectFromStringCommand(
					"MACHINE Empty END");
			defaultSS.execute(cmd, new StartAnimationCommand());
		} catch (BException e) {
			// TODO Implement
		}
		animations.registerAnimationChangeListener(this);
		incrementalUpdate = false;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/bconsole/index.html");
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		notifyModelChange(modelName);
	}

	public Object eval(final Map<String, String[]> params) {
		String line = get(params, "line");
		try {
			IEvalElement parsed = parse(line);
			String res = "";
			if (currentTrace == null) {
				EvaluationCommand cmd = parsed.getCommand(defaultSS.getRoot());
				defaultSS.execute(cmd);
				res = cmd.getValue().toString();
			} else {
				res = currentTrace.evalCurrent(parsed).toString();
			}
			return WebUtils.wrap("cmd", "BConsole.result", "result",
					StringEscapeUtils.escapeHtml(res));
		} catch (EvaluationException e) {
			return WebUtils.wrap("cmd", "BConsole.error", "error",
					"Not correct B syntax");
		}

	}

	public IEvalElement parse(final String line) {
		if (currentTrace == null) {
			return new ClassicalB(line);
		}
		return currentTrace.getModel().parseFormula(line);
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		if (currentAnimationChanged) {
			if (currentTrace == null) {
				modelName = null;
				notifyModelChange(modelName);
				this.currentTrace = currentTrace;
			} else if (currentTrace.getModel().getFormalismType() == FormalismType.B) {
				// ignore models that are not B models
				String modelName = currentTrace.getModel().getMainComponent()
						.toString();
				if (!modelName.equals(this.modelName)) {
					this.modelName = modelName;
					notifyModelChange(this.modelName);
				}
				this.currentTrace = currentTrace;
			}
		}
	}

	public void notifyModelChange(final String name) {
		submit(WebUtils.wrap("cmd", "BConsole.modelChange", "modelloaded",
				name != null, "name", name == null ? "" : name));
	}

	@Override
	public void animatorStatus(final boolean busy) {
		if (busy) {
			submit(WebUtils.wrap("cmd", "BConsole.disable"));
		} else {
			submit(WebUtils.wrap("cmd", "BConsole.enable"));
		}
	}

}
