package de.prob.worksheet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.worksheet.evaluators.GroovyEvaluator;
import de.prob.worksheet.evaluators.IdentityEvaluator;
import de.prob.worksheet.renderer.GroovyRenderer;
import de.prob.worksheet.renderer.MarkdownRenderer;
import de.prob.worksheet.renderer.StackTraceRenderer;

@Singleton
public class EditorFactory {

	private Api api;

	@Inject
	public EditorFactory(Api api) {
		this.api = api;
	}

	public Box createEditor(String language, String id, String content) {
		switch (EBoxTypes.valueOf(language)) {
		case groovy:
			return new Box(EBoxTypes.groovy, id, content, new GroovyRenderer(),
					new StackTraceRenderer(), new GroovyEvaluator());
			// case b:
			// return new BEditor(id, content);
		case markdown:
			return new Box(EBoxTypes.markdown, id, content,
					new MarkdownRenderer(), new MarkdownRenderer(),
					new IdentityEvaluator());
		case load:
			return new Box(EBoxTypes.load, id, content, new MarkdownRenderer(),
					new StackTraceRenderer(), new LoadEvaluator(api));
			// default:
			// return new DefaultEditor(id, content);
		}
		return null;
	}
}
