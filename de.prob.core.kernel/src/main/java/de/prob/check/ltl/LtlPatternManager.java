package de.prob.check.ltl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.prob.ltl.parser.pattern.Pattern;
import de.prob.ltl.parser.pattern.PatternManager;
import de.prob.ltl.parser.pattern.PatternUpdateListener;
import de.prob.web.WebUtils;

@Singleton
public class LtlPatternManager extends LtlEditor implements PatternUpdateListener {

	private final Logger logger = LoggerFactory.getLogger(LtlPatternManager.class);
	private PatternManager patternManager = new PatternManager();

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

}
