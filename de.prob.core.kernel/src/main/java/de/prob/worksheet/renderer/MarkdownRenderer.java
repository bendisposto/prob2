package de.prob.worksheet.renderer;

import org.pegdown.PegDownProcessor;

import de.prob.worksheet.IRenderer;
import de.prob.worksheet.WorkSheet;

public class MarkdownRenderer implements IRenderer {

	@Override
	public String render(WorkSheet context, Object content) {
		PegDownProcessor pegdown = context.getPegdown();
		return pegdown.markdownToHtml((String) content);
	}

}
