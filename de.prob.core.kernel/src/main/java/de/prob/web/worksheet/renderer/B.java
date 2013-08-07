package de.prob.web.worksheet.renderer;

import java.util.List;

import de.prob.web.views.Worksheet;

public class B extends AbstractRenderer {

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(String id, String text, Worksheet worksheet) {
		return pack(makeHtml(id, "B sayz " + text));
	}

}
