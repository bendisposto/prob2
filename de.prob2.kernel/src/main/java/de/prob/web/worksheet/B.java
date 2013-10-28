package de.prob.web.worksheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.web.WebUtils;

public class B extends AbstractBox {

	private String content = "";

	/*
		@SuppressWarnings("unchecked")
		@Override
		public List<Object> render(BindingsSnapshot snapshot) {
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
	*/

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		ScriptEngine groovy = owner.getGroovy();

		AnimationSelector animations = (AnimationSelector) groovy.getBindings(
				ScriptContext.GLOBAL_SCOPE).get("animations");
		if (animations.getTraces().size() == 0) {
			return pack(makeHtml(id, "*There is no animation started.*"));
		}

		ArrayList<Object> res = new ArrayList<Object>();

		StateSpace space = animations.getCurrentTrace().getStateSpace();
		StateId curStateId = animations.getCurrentTrace().getCurrentState();
		//
		if (!space.canBeEvaluated(curStateId)) {
			return pack(makeHtml(id, "*Current State can not be evaluated*"));
		}
		ArrayList<IEvalElement> evalElementList = new ArrayList<IEvalElement>();

		evalElementList.add(new EventB(content));
		List<EvaluationResult> evalResultList = space.eval(curStateId,
				evalElementList);
		if (evalResultList.size() > 1) {
			return pack(makeHtml(id, "*ProB returned multiple Results.*"));
		}
		if (evalResultList.size() == 0) {
			return pack(makeHtml(id, "*ProB returned no Results.*"));
		}

		EvaluationResult evalResult = evalResultList.get(0);
		if (evalResult.hasError()) {
			return pack(makeHtml(id,
					"*Evaluation Error: " + evalResult.getErrors() + "*"));
		}
		res.add(makeHtml(id, WebUtils.render("ui/worksheet/groovy_box.html",
				WebUtils.wrap("id", id, "result", evalResult, "output", ""))));
		return res;
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
