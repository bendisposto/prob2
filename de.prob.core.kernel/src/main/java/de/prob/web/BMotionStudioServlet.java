package de.prob.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.ajax.JSON;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.Home;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends HttpServlet {

	private String home;
	
	@Inject
	public BMotionStudioServlet(@Home String home) {
		this.home = home + "bms/";
	}

	private String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		reader.close();
		return stringBuilder.toString();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		String[] split = uri.split("/");
		String filename = split[split.length - 1];
		String filepath = home + filename;
		Object jsonFromFile = null;
		Object jsonFromUrl = null;
		
		Reader reader = new FileReader(filepath);
		
		// Get json from file
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			String extension = filename.substring(i + 1);
			String jsonfilename = filename.replace(extension, "json");
			File jsonfile = new File(home + jsonfilename);
			if (jsonfile.exists())
				jsonFromFile = JSON.parse(readFile(jsonfile.getPath()));
		}
		
		// Get json from url
		String json = req.getParameter("json");
		if (json != null) {
			jsonFromUrl = JSON.parse(json);
		}

		// TODO: This is ugly ... we need a better method to check the file type
		if (filename.endsWith(".html")
				&& (jsonFromFile != null || jsonFromUrl != null)) {
			String render = WebUtils
					.render(filepath, jsonFromFile, jsonFromUrl);
			reader = new StringReader(render);
		}

		ServletOutputStream outputStream = resp.getOutputStream();

		BufferedReader br = new BufferedReader(reader);
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
