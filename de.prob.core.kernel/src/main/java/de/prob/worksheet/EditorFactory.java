package de.prob.worksheet;

import de.prob.worksheet.boxtypes.BEditor;
import de.prob.worksheet.boxtypes.GroovyEditor;
import de.prob.worksheet.boxtypes.MarkdownEditor;

public class EditorFactory {

	public static DefaultEditor createEditor(String language, String id,
			String content) {
		switch (EBoxTypes.valueOf(language)) {
		case groovy:
			return new GroovyEditor(id, content);
		case b:
			return new BEditor(id, content);
		case markdown:
			return new MarkdownEditor(id, content);
		default:
			return new DefaultEditor(id, content);
		}
	}

}
