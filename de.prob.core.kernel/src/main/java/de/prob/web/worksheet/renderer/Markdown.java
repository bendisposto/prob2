package de.prob.web.worksheet.renderer;

import java.util.ArrayList;
import java.util.List;

import org.pegdown.PegDownProcessor;

import de.prob.web.WebUtils;
import de.prob.web.views.Worksheet;

public class Markdown extends AbstractRenderer {

	private final PegDownProcessor pegdown = new PegDownProcessor();

	@Override
	public List<Object> render(String id, String text, Worksheet ws) {
		ArrayList<Object> res = new ArrayList<Object>();
		String rendered = pegdown.markdownToHtml(text);
		if (rendered.isEmpty()) {
			rendered = "&nbsp;";
		}
		res.add(makeHtml(id, rendered));
		res.add(WebUtils.wrap("cmd", "Worksheet.renderMath", "box", id));
		return res;
	}

}
