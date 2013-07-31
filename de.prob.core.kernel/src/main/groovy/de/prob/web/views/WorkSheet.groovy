package de.prob.web.views

import de.prob.web.AbstractSession
import de.prob.web.WebUtils

class WorkSheet extends AbstractSession {

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		String uuid = getSessionUUID().toString();
		def template = "ui/templates/worksheet.html"
		def scope = ["uuid": uuid]
		return WebUtils.render(template, scope);
	}
}
