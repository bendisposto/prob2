package de.prob.web.views;

import java.util.Map;

import de.prob.web.AbstractSession;

public class Worksheet extends AbstractSession {

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/worksheet/index.html");
	}

	public Object reorder(Map<String, String[]> params) {
		String boxId = params.get("box")[0];
		int newpos = Integer.parseInt(params.get("newpos")[0]);
		System.out.println("Reodered box " + boxId + ". New position: "
				+ newpos);
		return null;
	}

}
