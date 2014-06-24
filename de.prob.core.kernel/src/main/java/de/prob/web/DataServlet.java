package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.inject.Singleton;

import de.prob.sync.UIState;

@Singleton
public class DataServlet extends HttpServlet {

	private static final Gson GSON = new Gson();

	private static final long serialVersionUID = -6568158351553781071L;
	Cache<Integer, Object> states = CacheBuilder.newBuilder()
			.expireAfterWrite(5, TimeUnit.SECONDS).build();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String state = req.getParameter("state");
		Object[] delta = UIState.delta(state);
		resp.setContentType("text/edn");
		PrintWriter writer = resp.getWriter();
		writer.write(delta[0].toString());
		writer.write('\n');
		writer.write(delta[1].toString());
		writer.flush();
		writer.close();
	}
}
