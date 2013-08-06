package de.prob.web.views;

import java.util.Map;

import javax.servlet.AsyncContext;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class Worksheet extends AbstractSession {

	private int boxcount = 0;

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

	@Override
	public void outOfDateCall(String client, int lastinfo, AsyncContext context) {
		super.outOfDateCall(client, lastinfo, context);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Worksheet.render_box",
				"number", String.valueOf(boxcount++), "type", "Groovy",
				"content", "GFY!");
		submit(wrap);
	}

}
