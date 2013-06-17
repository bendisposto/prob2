package de.prob.worksheet.renderer;

import org.pegdown.PegDownProcessor;

import de.prob.worksheet.IRenderer;
import de.prob.worksheet.WorkSheet;
import de.prob.worksheet.evaluators.GroovyResult;

public class GroovyRenderer implements IRenderer {

	@Override
	public String render(WorkSheet context, Object content) {

		PegDownProcessor pegdown = context.getPegdown();

		if (content instanceof GroovyResult) {
			GroovyResult result = (GroovyResult) content;
			return pegdown.markdownToHtml(result.result
					+ (!result.ouput.isEmpty() ? "<hr />" + result.ouput : ""));
		}
		return pegdown.markdownToHtml(content.toString());
	}

}
