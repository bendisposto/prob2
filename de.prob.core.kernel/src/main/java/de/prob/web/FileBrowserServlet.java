package de.prob.web;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class FileBrowserServlet extends HttpServlet {

	private final String userdir;

	@SuppressWarnings("unused")
	// Is used on JS side
	private final static class FileEntry {
		private final String path;
		private final boolean hidden;
		private final String name;

		public FileEntry(File f) {
			this(f, f.getName());
		}

		public FileEntry(File f, String name) {
			this.name = name;
			path = f.getAbsolutePath();
			hidden = f.isHidden();
		}

	}

	@SuppressWarnings("unused")
	private final class FileDataSet {
		private final List<FileEntry> files;
		private final List<FileEntry> dirs;
		private final String path;

		public FileDataSet(String path, List<FileEntry> dirs,
				List<FileEntry> files) {
			this.path = path;
			this.dirs = dirs;
			this.files = files;
		}
	}

	@Inject
	public FileBrowserServlet() {
		this.userdir = System.getProperty("user.home");
	}

	private final static Set<String> EXTENSIONS = new HashSet<String>(
			Arrays.asList((new String[] { "mch", "ref", "imp", "bum", "buc",
					"eventb" })));

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String remoteAddr = req.getRemoteAddr();
		if (!"127.0.0.1".equals(remoteAddr)) {
			resp.sendError(403);
		}
		String path = req.getParameter("path");
		String check = req.getParameter("check");

		if (path != null && !path.isEmpty()) {
			File f = new File(path);
			if (check != null) {
				PrintWriter writer = resp.getWriter();
				writer.println(validProBFile(f));
				writer.close();
				return;
			}
			ArrayList<FileEntry> files = new ArrayList<FileEntry>();
			ArrayList<FileEntry> dirs = new ArrayList<FileEntry>();

			if (!f.isDirectory()) {
				f = f.getParentFile();
			}

			FileFilter filter = makeFilter();
			File[] listFiles = f.listFiles(filter);
			if (f.getParentFile() != null) {
				dirs.add(new FileEntry(f.getParentFile(), ".."));
			}
			for (File file : listFiles) {
				if (file.isFile() && file.canRead()) {
					files.add(new FileEntry(file));
				}
				if (file.isDirectory() && file.canRead() && file.canExecute()) {
					dirs.add(new FileEntry(file));
				}

			}

			String filesStr = WebUtils
					.toJson(new FileDataSet(path, dirs, files));

			PrintWriter writer = resp.getWriter();
			writer.println(filesStr);
			writer.close();

		} else {
			if (check != null) {
				PrintWriter writer = resp.getWriter();
				writer.println(false);
				writer.close();
			} else {
				resp.sendRedirect("/files/?path=" + userdir);
			}
		}
	}

	public static boolean validProBFile(File f) {
		return f.exists()
				&& f.isFile()
				&& f.canRead()
				&& EXTENSIONS.contains(Files.getFileExtension(f
						.getAbsolutePath()));
	}

	private FileFilter makeFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| EXTENSIONS.contains(Files.getFileExtension(f
								.getName()));
			}
		};
	}

}
