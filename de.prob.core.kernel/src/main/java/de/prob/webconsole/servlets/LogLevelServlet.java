package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.impl.StaticLoggerBinder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.google.gson.Gson;
import com.google.inject.Singleton;

import de.prob.webconsole.ResultObject;

@SuppressWarnings("serial")
@Singleton
public class LogLevelServlet extends HttpServlet {


	private ResultObject setLogLevel(Level level) {
		ResultObject result = new ResultObject();
		StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
		LoggerContext loggerFactory = (LoggerContext) singleton
				.getLoggerFactory();
		Logger root = (Logger) loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(level);
		result.setOutput(level.levelStr);
		return result;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String input = req.getParameter("input");
		
		Level level = Level.valueOf(input);
		
		sendResult(out, setLogLevel(level));
	}

	private void sendResult(PrintWriter out, ResultObject result) {
		Gson g = new Gson();
		String json = g.toJson(result);
		out.println(json);
		out.close();
	}
}