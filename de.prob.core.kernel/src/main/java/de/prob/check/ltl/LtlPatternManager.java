package de.prob.check.ltl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Singleton;

import de.prob.ltl.parser.pattern.Pattern;
import de.prob.ltl.parser.pattern.PatternManager;
import de.prob.ltl.parser.pattern.PatternUpdateListener;
import de.prob.web.WebUtils;

@Singleton
public class LtlPatternManager extends LtlEditor implements PatternUpdateListener {

	private final Logger logger = LoggerFactory.getLogger(LtlPatternManager.class);

	public LtlPatternManager() {
		super();
		patternManager.addUpdateListener(this);
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/manager/manager.html");
	}

	@Override
	public void patternUpdated(Pattern pattern, PatternManager patternManager) {

	}

	public Object getPatternList(Map<String, String[]> params) {
		logger.trace("Get current pattern list");

		List<PatternInfo> patterns = new LinkedList<PatternInfo>();
		for (Pattern pattern: patternManager.getBuiltins()) {
			patterns.add(new PatternInfo(pattern.getName(), pattern.getDescription(), pattern.getCode(), pattern.isBuiltin()));
		}
		for (Pattern pattern: patternManager.getPatterns()) {
			patterns.add(new PatternInfo(pattern.getName(), pattern.getDescription(), pattern.getCode(), pattern.isBuiltin()));
		}

		return WebUtils.wrap(
				"cmd", "LtlPatternManager.setPatternList",
				"patterns", WebUtils.toJson(patterns));
	}

	public Object savePattern(Map<String, String[]> params) {
		logger.trace("Save pattern");

		String name = get(params, "name");
		String description = get(params, "description");
		String code = get(params, "code");

		Pattern pattern = new Pattern();
		pattern.setBuiltin(false);
		pattern.setName(name);
		pattern.setDescription(description);
		pattern.setCode(code);
		patternManager.addPattern(pattern);

		return WebUtils.wrap(
				"cmd", "LtlPatternManager.saveSuccess",
				"pattern", WebUtils.toJson(new PatternInfo(name, description, code, false)));
	}

	public Object updatePattern(Map<String, String[]> params) {
		logger.trace("Update pattern");

		String oldName = get(params, "oldName");
		String name = get(params, "name");
		String description = get(params, "description");
		String code = get(params, "code");

		Pattern pattern = patternManager.getUserPattern(oldName);
		if (pattern != null) {
			pattern.setBuiltin(false);
			pattern.setName(name);
			pattern.setDescription(description);
			pattern.setCode(code);
			pattern.notifyUpdateListeners();
		}
		return WebUtils.wrap(
				"cmd", "LtlPatternManager.updateSuccess",
				"oldPatternName", oldName,
				"pattern", WebUtils.toJson(new PatternInfo(name, description, code, false)));
	}

	public Object removePatterns(Map<String, String[]> params) {
		logger.trace("Remove patterns");

		String names[] = getArray(params, "names");

		for (String name : names) {
			Pattern pattern = patternManager.getUserPattern(name);
			patternManager.removePattern(pattern);
		}

		return WebUtils.wrap(
				"cmd", "LtlPatternManager.removeSuccess",
				"names", WebUtils.toJson(names));
	}

	private String[] getArray(Map<String, String[]> params, String key) {
		String[] temp = params.get(key);
		Gson GSON = new Gson();

		String array[] = GSON.fromJson(temp[0], String[].class);
		return array;
	}

}
