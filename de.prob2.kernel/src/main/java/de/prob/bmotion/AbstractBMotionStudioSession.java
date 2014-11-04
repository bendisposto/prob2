package de.prob.bmotion;

import de.prob.ui.api.ITool;
import de.prob.web.AbstractSession;
import de.prob.web.ISession;
import de.prob.web.data.SessionResult;

import javax.servlet.AsyncContext;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractBMotionStudioSession extends AbstractSession {

    private final ITool tool;
    private String templatePath;
    private int port;
    private String host;
    private Map<String, String> parameterMap = new HashMap<String, String>();

    public AbstractBMotionStudioSession(UUID id, ITool tool, String templatePath, String host, int port) {
        super(id);
        this.tool = tool;
        this.templatePath = templatePath;
        this.host = host;
        this.port = port;
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String> params) {
        this.parameterMap = params;
    }

    public void addParameter(final String key, final String value) {
        parameterMap.put(key, value);
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

    public Object executeCommand(
            final Map<String, String[]> parameterMap) {
        String cmd = get(parameterMap, "cmd");
        final ISession delegate = this;
        Class<? extends AbstractSession> clazz = this.getClass();
        try {
            final Method method = clazz.getMethod(cmd, Map.class);
            Object result = method.invoke(delegate, parameterMap);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
