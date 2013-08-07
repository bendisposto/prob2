package de.prob.web.worksheet.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.prob.web.WebUtils;

public abstract class AbstractRenderer implements IRender {

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

	@Override
	public String getTemplate() {
		return "/ui/worksheet/box.html";
	}

	@Override
	public boolean useCodemirror() {
		return true;
	}

	@Override
	public Map<String, String> getExtraInfo() {
		return Collections.emptyMap();
	}

	@Override
	public String initialContent() {
		return "";
	}
}
