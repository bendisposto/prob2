package de.prob.web.worksheet.renderer;

import java.util.List;

import de.prob.web.views.Worksheet;

public class LoadModel extends AbstractRenderer {

	@Override
	public List<Object> render(String id, String text, Worksheet worksheet) {
		return null;
	}

	@Override
	public String getTemplate() {
		return "/ui/worksheet/load_model.html";
	}

	@Override
	public String initialContent() {
		return System.getProperty("user.home");
	}

	@Override
	public boolean useCodemirror() {
		return false;
	}

}
