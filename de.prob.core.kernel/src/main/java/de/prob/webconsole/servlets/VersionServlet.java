package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.IAnimator;
import de.prob.animator.command.GetVersionCommand;
import de.prob.cli.CliVersionNumber;
import de.prob.cli.ProBInstanceProvider;
import de.prob.scripting.Downloader;
import de.prob.webconsole.ServletContextListener;

@SuppressWarnings("serial")
@Singleton
public class VersionServlet extends HttpServlet {

	private final ProBInstanceProvider instanceProvider;
	private final Object versions;
	private CliVersionNumber installed;

	@Inject
	public VersionServlet(Downloader downloader,
			ProBInstanceProvider instanceProvider) {
		versions = downloader.availableVersions();
		this.instanceProvider = instanceProvider;

	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		PrintWriter out = res.getWriter();

		boolean binaryPresent = false;
		try {
			instanceProvider.get();
			binaryPresent = true;
		} catch (Exception e) {
			binaryPresent = false;
		}

		if (binaryPresent && installed == null) {
			GetVersionCommand versionCommand = new GetVersionCommand();
			IAnimator animator = ServletContextListener.INJECTOR
					.getInstance(IAnimator.class);
			animator.execute(versionCommand);
			installed = versionCommand.getVersion();
		}

		result.put("installed", installed);

		result.put("available", versions);

		Gson g = new Gson();
		String json = g.toJson(result);
		out.println(json);
		out.close();

	}

}
