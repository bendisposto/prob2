package de.prob.animator.domainobjects;

import com.google.gson.Gson;
import com.google.inject.Singleton;

@Singleton
public class EvalElementFactory {

	public IEvalElement deserialize(final String content) {
		if (content.startsWith("#ClassicalB:")) {
			return toClassicalB(content);
		}
		if (content.startsWith("#EventB:")) {
			return toEventB(content);
		}
		if (content.startsWith("#CSP:")) {
			return toCSP(content);
		}

		throw new IllegalArgumentException("String with format " + content
				+ " cannot be deserialized to an IEvalElement");
	}

	private EventB toEventB(final String content) {
		return new EventB(content.substring(content.indexOf(":") + 1));
	}

	private ClassicalB toClassicalB(final String content) {
		return new ClassicalB(content.substring(content.indexOf(":") + 1));
	}

	private CSP toCSP(final String content) {
		String objects = content.substring(content.indexOf(":") + 1);

		Gson g = new Gson();
		CSP fromJson = g.fromJson(objects, CSP.class);

		return fromJson;
	}
}
