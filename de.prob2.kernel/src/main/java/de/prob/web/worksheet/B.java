package de.prob.web.worksheet;

import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.model.representation.AbstractModel;

public class B extends AbstractBox {

	private String content = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(final BindingsSnapshot snapshot) {
		ScriptEngine groovy = owner.getGroovy();
		AbstractModel model = (AbstractModel) groovy.getBindings(
				ScriptContext.GLOBAL_SCOPE).get("model");
		Object store = groovy.getBindings(ScriptContext.GLOBAL_SCOPE).get(
				"store");
		if (store instanceof Long) {
			Long storeid = (Long) store;
			EvalstoreEvalCommand c = new EvalstoreEvalCommand(storeid,
					new ClassicalB(content));
			model.getStateSpace().execute(c);
			EvalstoreResult r = c.getResult();
			String value = r.getResult() instanceof EvalResult ? ((EvalResult) r
					.getResult()).getValue() : "error";
			return pack(makeHtml(id, value));
		} else {
			return pack(makeHtml(id,
					"*Could not find evaluation context. Maybe you need to load a model*"));
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
