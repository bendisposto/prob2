package de.prob.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
public class ContentServlet extends HttpServlet {

	private class PartList extends ArrayList<String> {

		private static final long serialVersionUID = -5668244262489304794L;

		public PartList(final String[] split) {
			super(Arrays.asList(split));
		}

		@Override
		public String get(final int index) {
			if (index >= this.size()) {
				return "";
			} else {
				return super.get(index);
			}
		}

	}

	private void send(final HttpServletResponse resp, final String html)
			throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.write(html);
		writer.flush();
		writer.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		List<String> parts = new PartList(uri.split("/"));

		String file = parts.get(2);
		if (!file.endsWith(".html"))
			file += ".html";
		String session = parts.get(3);

		String html = WebUtils.render("nui/" + file, new Object[0]);
		send(resp, html);
	}

}
