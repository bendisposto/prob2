package de.prob.bmotion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.AsyncContext;

import de.prob.ui.api.ITool;
import de.prob.web.AbstractSession;

public abstract class AbstractBMotionStudioSession extends AbstractSession {

    private String templatePath;

    private int port;

    private String host;

    private Map<String, String> parameterMap = new HashMap<String, String>();

    private final ITool tool;

    public AbstractBMotionStudioSession(UUID id, ITool tool,
                                        String templatePath, String host, int port) {
        super(id);
        this.tool = tool;
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

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public ITool getTool() {
        return tool;
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
