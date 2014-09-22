package de.prob.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.Home;

@SuppressWarnings("serial")
@Singleton
public class WorkspaceServlet extends HttpServlet {

	private String home;

	@Inject
	public WorkspaceServlet(@Home String home) {
		this.home = home + "/bms/";
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		String[] split = uri.split("/");
		String filename = home + split[split.length - 1];
		System.out.println(filename);
		ServletOutputStream outputStream = resp.getOutputStream();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {

			String line;
			while ((line = br.readLine()) != null) {
				outputStream.println(line);
			}

		} finally {
			br.close();
		}

		outputStream.flush();
		outputStream.close();

	}
}
