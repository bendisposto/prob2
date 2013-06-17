package de.prob.worksheet.renderer;

import de.prob.worksheet.IRenderer;
import de.prob.worksheet.WorkSheet;

public class StackTraceRenderer implements IRenderer {

	public String render(WorkSheet context, Throwable t) {
		return t.getMessage();
	}

	// private String cleanGroovyException(ScriptException e) {
	// String message = e.getMessage();
	// if (e.getCause() instanceof MultipleCompilationErrorsException)
	// return message.replaceAll("(.*\n.*Script.*?groovy): ", "");
	// if (e.getCause().getCause() instanceof MissingPropertyException) {
	// String r1 = message.replaceAll(".*property:", "No such property: ");
	// String r2 = r1.replaceAll("for.*", "");
	// return r2;
	// }
	// return message;
	// }

	@Override
	public String render(WorkSheet context, Object content) {
		if (content instanceof Throwable) {
			Throwable t = (Throwable) content;
			return render(context, t);
		}
		return content.toString();
	}
}
