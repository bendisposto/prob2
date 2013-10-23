package de.prob.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
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
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB
	
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

		req.setCharacterEncoding("UTF-8");

		String uri = req.getRequestURI();
		String furl = uri.replace("/bms/", "");
		String[] split = furl.split("/");
		String filename = split[split.length - 1];
		String filepath = home + furl;
		Object jsonFromFile = null;
		Object jsonFromUrl = null;

//		Reader reader = new FileReader(filepath);

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

		// Set correct mimeType
		String mimeType = getServletContext().getMimeType(filepath);
		resp.setContentType(mimeType);
		
		// Prepare streams.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		InputStream stream = null;
		
		// // TODO: This is ugly ... we need a better method to check the file
		// type
		if (filename.endsWith(".html")
				&& (jsonFromFile != null || jsonFromUrl != null)) {

			String render = WebUtils
					.render(filepath, jsonFromFile, jsonFromUrl);
			stream = new ByteArrayInputStream(render.getBytes());

		} else {
			stream = new FileInputStream(filepath);
		}

		try {
			// Open streams.
			input = new BufferedInputStream(stream, DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(resp.getOutputStream(),
					DEFAULT_BUFFER_SIZE);
			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			// Gently close streams.
			close(output);
			close(input);
		}


	}

	private void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				// Do your thing with the exception. Print it, log it or mail
				// it.
				e.printStackTrace();
			}
		}
	}
	
}
