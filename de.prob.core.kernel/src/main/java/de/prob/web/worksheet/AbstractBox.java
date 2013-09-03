package de.prob.web.worksheet;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;

import de.prob.web.WebUtils;
import de.prob.web.views.Worksheet;

public abstract class AbstractBox implements IBox {

	protected String id;
	protected Worksheet owner;
	protected Bindings bindings = null;

	protected Map<String, String> makeHtml(String id, String html) {
		return WebUtils
				.wrap("cmd", "Worksheet.render", "box", id, "html", html);
	}

	protected List<Object> pack(Map<String, String>... maps) {
		ArrayList<Object> res = new ArrayList<Object>();
		for (Map<String, String> map : maps) {
			res.add(map);
		}
		return res;
	}

	protected void freezeBindings() {
		owner.getGroovy().getBindings(ScriptContext.ENGINE_SCOPE);
	}

	@Override
	public void setId(String id) {
		checkState(this.id == null);
		this.id = id;
	}

	@Override
	public void setOwner(Worksheet owner) {
		checkState(this.owner == null);
		this.owner = owner;
	}

	private Map<String, String> create(String cmd) {
		Map<String, String> m = new HashMap<String, String>();
		m.putAll(getAdditionalEntries());
		m.putAll(WebUtils.wrap("number", id, "type", this.getClass()
				.getSimpleName(), "content", getContentAsJson(),
				"renderedhtml", "", "template", getTemplate(), "codemirror",
				String.valueOf(useCodemirror())));
		m.put("cmd", "Worksheet." + cmd);
		return m;
	}

	protected abstract String getContentAsJson();

	protected Map<String, String> getAdditionalEntries() {
		return Collections.emptyMap();
	}

	protected boolean useCodemirror() {
		return true;
	}

	protected String getTemplate() {
		return "/ui/worksheet/boxes/codemirror.html";
	}

	@Override
	public Map<String, String> createMessage() {
		return create("render_box");
	}

	@Override
	public Map<String, String> replaceMessage() {
		return create("replace_box");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.FULL_REEVALUATION; // this is always safe
	}

	@Override
	public boolean requiresReEvaluation() {
		return true;
	}

}
