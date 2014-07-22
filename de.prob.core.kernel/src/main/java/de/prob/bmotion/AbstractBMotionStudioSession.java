package de.prob.bmotion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.AsyncContext;

import de.prob.model.representation.AbstractModel;
import de.prob.web.AbstractSession;

public abstract class AbstractBMotionStudioSession extends AbstractSession {

	private String templatePath;

	private UUID id;

	private AbstractModel model;

	private int port;

	private String host;

	private Map<String, String> parameterMap = new HashMap<String, String>();

	public AbstractBMotionStudioSession(String templatePath,
			AbstractModel model, String host, int port) {
		this.id = UUID.randomUUID();
		this.model = model;
		this.templatePath = templatePath;
		this.host = host;
		this.port = port;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}
	
	public void addParameter(final String key, final String value) {
		parameterMap.put(key, value);
	}
	
	public void setParameterMap(Map<String, String> params) {
		this.parameterMap = params;
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


	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return null;
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
	}
	
}
