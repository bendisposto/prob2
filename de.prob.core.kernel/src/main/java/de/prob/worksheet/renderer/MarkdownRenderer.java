package de.prob.worksheet.renderer;

import org.pegdown.PegDownProcessor;

import de.prob.worksheet.IRenderer;
import de.prob.worksheet.WorkSheet;

public class MarkdownRenderer implements IRenderer {

	@Override
	public String render(WorkSheet context, Object content) {
		PegDownProcessor pegdown = context.getPegdown();
		String text = (String) content;
		String rendered = pegdown.markdownToHtml(text);
		if (rendered.isEmpty())
			rendered = "&nbsp;";
		return rendered;
	}

}
