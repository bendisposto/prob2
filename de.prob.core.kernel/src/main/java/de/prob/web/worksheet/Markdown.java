package de.prob.web.worksheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pegdown.PegDownProcessor;

import de.prob.web.WebUtils;

public class Markdown extends AbstractBox {

	private final PegDownProcessor pegdown = new PegDownProcessor();
	private String content = "";

	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		ArrayList<Object> res = new ArrayList<Object>();
		String rendered = pegdown.markdownToHtml(content);
		if (rendered.isEmpty()) {
			rendered = "&nbsp;";
		}
		res.add(makeHtml(id, rendered));
		res.add(WebUtils.wrap("cmd", "Worksheet.renderMath", "box", id));
		return res;
	}

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	// Markdown boxes can be moved arbitrarily
	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.DONT_CARE;
	}

	// Even when re-evaluating, we don't have to mess with Markdown boxes
	@Override
	public boolean requiresReEvaluation() {
		return false;
	}

}
