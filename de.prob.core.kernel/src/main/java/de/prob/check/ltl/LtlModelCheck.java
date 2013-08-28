package de.prob.check.ltl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class LtlModelCheck extends LtlPatternManager {

	private final Logger logger = LoggerFactory.getLogger(LtlModelCheck.class);


	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/index.html");
	}

}
