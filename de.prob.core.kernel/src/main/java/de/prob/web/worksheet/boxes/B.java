package de.prob.web.worksheet.boxes;

import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.AbstractModel;
import de.prob.web.views.BindingsSnapshot;

public class B extends AbstractBox {

	private String content = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		ScriptEngine groovy = owner.getGroovy();
		AbstractModel model = (AbstractModel) groovy.getBindings(
				ScriptContext.GLOBAL_SCOPE).get("model");
		Object store = groovy.getBindings(ScriptContext.GLOBAL_SCOPE).get(
				"store");
		if (store instanceof Long) {
			Long storeid = (Long) store;
			EvalstoreEvalCommand c = new EvalstoreEvalCommand(storeid,
					new ClassicalB(content));
			model.getStatespace().execute(c);
			EvalstoreResult r = c.getResult();
			String value = r.getResult().value;
			return pack(makeHtml(id, value));
		} else
			return pack(makeHtml(id,
					"*Could not find evaluation context. Maybe you need to load a model*"));
	}

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
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
