package de.prob.check.ltl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.prob.ltl.parser.LtlParser;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class LtlEditor extends AbstractSession {

	private final Logger logger = LoggerFactory.getLogger(LtlEditor.class);

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/editor.html");
	}

	public Object parseInput(Map<String, String[]> params) {
		logger.trace("Parse ltl formula");
		String input = get(params, "input");

		ParseListener listener = new ParseListener();
		parse(input, listener);

		Map<String, String> result = null;
		if (listener.getErrorMarkers().size() == 0) {
			logger.trace("Parse ok (errors: 0, warnings: {}). Submitting parse results", listener.getWarningMarkers().size());
			result = WebUtils.wrap(
					"cmd", "LtlEditor.parseOk",
					"warnings", WebUtils.toJson(listener.getWarningMarkers()));
		} else {
			logger.trace("Parse failed (errors: {}, warnings: {}). Submitting parse results", listener.getErrorMarkers().size(), listener.getWarningMarkers().size());
			result = WebUtils.wrap(
					"cmd", "LtlEditor.parseFailed",
					"warnings", WebUtils.toJson(listener.getWarningMarkers()),
					"errors", WebUtils.toJson(listener.getErrorMarkers()));
		}

		return result;
	}

	private void parse(String input, ParseListener listener) {
		LtlParser parser = new LtlParser(input);
		parser.removeErrorListeners();
		parser.addErrorListener(listener);
		parser.addWarningListener(listener);

		parser.parse();
	}

}
