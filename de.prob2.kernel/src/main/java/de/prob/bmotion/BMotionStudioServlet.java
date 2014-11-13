package de.prob.bmotion;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.prob.Main;
import de.prob.scripting.Api;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.statespace.AnimationSelector;
import de.prob.ui.api.ITool;
import de.prob.ui.api.ToolRegistry;
import de.prob.web.WebUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends AbstractBMotionStudioServlet {

    private final ScriptEngineProvider engineProvider;

    @Inject
    public BMotionStudioServlet(final Api api, final AnimationSelector animations, final ToolRegistry toolRegistry,
                                final ScriptEngineProvider engineProvider,
                                final VisualisationRegistry visualisationRegistry) {
        super(api, animations, toolRegistry, visualisationRegistry);
        this.engineProvider = engineProvider;
    }

    private void update(HttpServletRequest req, AbstractBMotionStudioSession bmsSession) {
        int lastinfo = Integer.parseInt(req.getParameter("lastinfo"));
        String client = req.getParameter("client");
        bmsSession.sendPendingUpdates(client, lastinfo, req.startAsync());
    }

    //TODO: check if returned result of command is a json object
    private void executeCommand(HttpServletRequest req, HttpServletResponse resp, AbstractBMotionStudioSession bms)
            throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        Object result = bms.executeCommand(parameterMap);
        if(result != null) {
            resp.setContentType("application/json");
            toOutput(resp, WebUtils.toJson(result));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AbstractBMotionStudioSession bms = initSession(req, resp);
        String modeParameter = req.getParameter("mode");
        if ("update".equals(modeParameter)) {
            update(req, bms);
        } else if ("command".equals(modeParameter)) {
            executeCommand(req, resp, bms);
        } else {
            // Else (should be) file request
            delegateFileRequest(req, resp, bms);
        }
        return;
    }

    private String getBaseHtml(AbstractBMotionStudioSession bms) {
        String templatePath = bms.getTemplatePath();
        String fileName = new File(templatePath).getName();
        Object scope = WebUtils
                .wrap("clientid", bms.getSessionUUID().toString(), "port", bms.getPort(), "host", bms.getHost(),
                        "template", templatePath, "templatefile", fileName, "standalone", Main.standalone ? "yes" : "");
        return WebUtils.render("ui/bmsview/index.html", scope);
    }

    @Override
    protected String getDefaultPage(AbstractBMotionStudioSession bms) {

        // Get template document
        String templateHtml = BMotionUtil.readFile(BMotionUtil.getFullTemplatePath(bms.getTemplatePath()));
        Document templateDocument = Jsoup.parse(templateHtml);
        templateDocument.outputSettings().prettyPrint(false);
        Elements templateHead = templateDocument.getElementsByTag("head");
        Elements templateBody = templateDocument.getElementsByTag("body");

        // Get base document
        String baseHtml = getBaseHtml(bms);
        Document baseDocument = Jsoup.parse(baseHtml);
        baseDocument.outputSettings().prettyPrint(false);
        Elements baseHead = baseDocument.getElementsByTag("head");
        Element baseBody = baseDocument.getElementById("bmsVisualisation");
        baseHead.append(templateHead.html());
        baseBody.append(templateBody.html());
        // Workaround, since jsoup renames svg image tags to img
        // tags ...
        for (Element e : baseDocument.getElementsByTag("svg")) {
            Elements imgTags = e.getElementsByTag("img");
            imgTags.tagName("image");
        }
        return baseDocument.html();

    }

    @Override
    protected AbstractBMotionStudioSession createSession(UUID id, ITool tool,
                                                         Map<String, BMotionComponent> proBMotionElementMap,
                                                         String template, String host, int port) {
        return new BMotion(id, tool, proBMotionElementMap, toolRegistry, template, engineProvider, host, port);
    }

}
