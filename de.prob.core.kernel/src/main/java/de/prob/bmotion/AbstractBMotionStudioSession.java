package de.prob.bmotion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.prob.model.representation.AbstractModel;
import de.prob.scripting.Api;
import de.prob.web.AbstractSession;

public abstract class AbstractBMotionStudioSession extends AbstractSession {

	private String templatePath;

	private UUID id;

	private AbstractModel model;

	private int port;

	private String host;

	private final Api api;

	private final Map<String, Object> parameterMap = new HashMap<String, Object>();

	public AbstractBMotionStudioSession(final Api api) {
		this.id = UUID.randomUUID();
		this.api = api;
	}

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public String getTemplateFolder() {
		String template = getTemplatePath();
		if (template != null) {
			return new File(template).getParent();
		}
		return null;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public UUID getSessionUUID() {
		return id;
	}

	public void setSessionUUID(UUID id) {
		this.id = id;
	}

	public void addParameter(final String key, final Object value) {
		parameterMap.put(key, value);
	}

	public AbstractModel getModel() {
		return model;
	}

	public void setModel(AbstractModel model) {
		this.model = model;
	}

	protected String getFormalism(String machinePath) {

		String lang = null;
		if (machinePath.endsWith(".csp")) {
			return "csp";
		} else if (machinePath.endsWith(".buc") || machinePath.endsWith(".bcc")
				|| machinePath.endsWith(".bum") || machinePath.endsWith(".bcm")) {
			return "eventb";
		} else if (machinePath.endsWith(".mch")) {
			return "b";
		} else if (machinePath.endsWith(".tla")) {
			return "tla";
		}
		return lang;

	}

	public Api getApi() {
		return api;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public abstract void initSession();

}
