package de.prob.bmotion;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.web.AbstractSession;

public class AbstractBMotionStudioSession extends AbstractSession {


	Logger logger = LoggerFactory.getLogger(BMotionStudioEditorSession.class);

	private String templatePath;

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return null;
	}

	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

	public String getTemplate() {
		return templatePath;
	}
	
}
