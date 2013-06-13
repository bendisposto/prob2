package de.prob.worksheet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.worksheet.boxtypes.BEditor;
import de.prob.worksheet.boxtypes.GroovyEditor;
import de.prob.worksheet.boxtypes.LoadModelEditor;
import de.prob.worksheet.boxtypes.MarkdownEditor;

@Singleton
public class EditorFactory {

	private Api api;

	@Inject
	public EditorFactory(Api api) {
		this.api = api;
	}

	public DefaultEditor createEditor(String language, String id, String content) {
		switch (EBoxTypes.valueOf(language)) {
		case groovy:
			return new GroovyEditor(id, content);
		case b:
			return new BEditor(id, content);
		case markdown:
			return new MarkdownEditor(id, content);
		case load:
			return new LoadModelEditor(id, content, api);
		default:
			return new DefaultEditor(id, content);
		}
	}

}
