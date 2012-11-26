package de.prob.webconsole.servlets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.Home;

@SuppressWarnings("serial")
@Singleton
public class ScrollbackServlet extends HttpServlet {

	private String home;

	@Inject
	public ScrollbackServlet(@Home String home) {
		this.home = home + "scrollback";
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		String[] r = readFile(home);

		LinkedHashSet<String> copy = new LinkedHashSet<String>();
		copy.addAll(Arrays.asList(r));

		write(copy);

		Gson g = new Gson();
		String json = g.toJson(copy);
		out.println(json);
		out.close();
	}

	private void write(LinkedHashSet<String> copy) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		final File scrollback = new File(home);
		try {
			fw = new FileWriter(scrollback, false);
			bw = new BufferedWriter(fw);
			for (String input : copy) {
				bw.write(input);
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {

		} finally {
			try {
				if (fw != null)
					fw.close();
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				// who cares
			}
		}

	}

	private String[] readFile(String home2) {
		List<String> res = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(home);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				res.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		return res.toArray(new String[res.size()]);
	}

}