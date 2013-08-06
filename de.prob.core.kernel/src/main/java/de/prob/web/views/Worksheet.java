package de.prob.web.views;

import java.util.Map;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class Worksheet extends AbstractSession {

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		Object scope = WebUtils.wrap("clientid", clientid, "default-box-type",
				"Groovy");
		String render = WebUtils.render("ui/worksheet/index.html", scope);
		return render;
	}

	public Object reorder(Map<String, String[]> params) {
		String boxId = params.get("box")[0];
		int newpos = Integer.parseInt(params.get("newpos")[0]);
		System.out.println("Reodered box " + boxId + ". New position: "
				+ newpos);
		return null;
	}

}
