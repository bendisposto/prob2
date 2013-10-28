package de.prob.web.worksheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.web.WebUtils;

public class B extends AbstractBox {

	private String content = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(final BindingsSnapshot snapshot) {
		ScriptEngine groovy = owner.getGroovy();
		AbstractModel model = (AbstractModel) groovy.getBindings(
				ScriptContext.ENGINE_SCOPE).get("model");
		Object store = groovy.getBindings(ScriptContext.ENGINE_SCOPE).get(
				"store");
		AnimationSelector animations = (AnimationSelector) groovy.getBindings(
				ScriptContext.GLOBAL_SCOPE).get("animations");
		if (model == null) {
			return pack(makeHtml(id,
					"*Could not find evaluation context. Maybe you need to load a model*"));
		} else {
			ArrayList<Object> res = new ArrayList<Object>();
			StateSpace space = animations.getCurrentTrace().getStateSpace();
			if (!(store instanceof Long)) {
				// Get the current State
				String stateId = animations.getCurrentTrace().getCurrentState()
						.getId();
				// Create a new EvalStore
				EvalstoreCreateByStateCommand cmd = new EvalstoreCreateByStateCommand(
						stateId);
				space.execute(cmd);
				store = cmd.getEvalstoreId();
			}
			if (store == null) {
				return pack(makeHtml(id,
						"*Could not create Eval Store. Do sth.*"));
			} else {
				try {
					// Evaluate the expression
					IEvalElement eval = new EventB(content);
					EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(
							(Long) store, eval);
					space.execute(cmd);
					EvalstoreResult storeResult = cmd.getResult();
					if (storeResult.isSuccess()) {
						store = storeResult.getResultingStoreId();
						String result = storeResult.getResult().getValue();
						String output = "";
						res.add(makeHtml(id, WebUtils.render(
								"ui/worksheet/groovy_box.html", WebUtils.wrap(
										"id", id, "result", result, "output",
										output))));
						groovy.getBindings(ScriptContext.ENGINE_SCOPE).put(
								"store", store);

					} else {
						if (storeResult.hasInterruptedOccurred()) {
							res.add(makeHtml(
									id,
									WebUtils.render(
											"ui/worksheet/groovy_exception.html",
											WebUtils.wrap(
													"id",
													id,
													"message",
													"The evaluation has been interrupted",
													"stacktrace", ""))));
						}
						if (storeResult.hasTimeoutOccurred()) {
							res.add(makeHtml(id, WebUtils.render(
									"ui/worksheet/groovy_exception.html",
									WebUtils.wrap("id", id, "message",
											"The evaluation has timed out",
											"stacktrace", ""))));
						}
						if (storeResult.getResult().hasError()) {
							res.add(makeHtml(id, WebUtils.render(
									"ui/worksheet/groovy_exception.html",
									WebUtils.wrap("id", id, "message",
											"No Success Result Error: "
													+ storeResult.getResult()
															.getErrors(),
											"stacktrace", ""))));
						}
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						res.add(makeHtml(id, WebUtils.render(
								"ui/worksheet/groovy_exception.html",
								WebUtils.wrap(
										"id",
										id,
										"message",
										"No Success Result Error: "
												+ e.getLocalizedMessage(),
										"stacktrace", ""))));
					} else {
						res.add(makeHtml(
								id,
								WebUtils.render(
										"ui/worksheet/groovy_exception.html",
										WebUtils.wrap(
												"id",
												id,
												"message",
												"ProB has thrown an exception maybe your expression is not correctly spelled.",
												"stacktrace", ""))));
					}
				}
				return res;
			}
		}
	}

	@Override
	public void setContent(final Map<String, String[]> data) {
		content = data.get("text")[0];
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.EVERYTHING_BELOW;
	}
}
