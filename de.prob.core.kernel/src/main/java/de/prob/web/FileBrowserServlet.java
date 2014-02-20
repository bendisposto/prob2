package de.prob.web;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class FileBrowserServlet extends HttpServlet {

	private final String userdir;
	private final Logger logger = LoggerFactory
			.getLogger(FileBrowserServlet.class);

	private final static Set<String> PROB_FILES = new HashSet<String>(
			Arrays.asList((new String[] { "mch", "ref", "imp", "bum", "buc",
					"eventb" })));

	private final static Set<String> BMS_FILES = new HashSet<String>(
			Arrays.asList((new String[] { "htm", "html", "svg" })));
	
	private final static Map<String, Set<String>> EXTENSIONS = new HashMap<String, Set<String>>();

	static {
		EXTENSIONS.put("prob", PROB_FILES);
		EXTENSIONS.put("bms", BMS_FILES);
	}

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
			path = f.getAbsolutePath().replace("\\", "\\\\");
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getParameter("path");
		String workspace = req.getParameter("workspace");
		String check = req.getParameter("check");
		String extensionSet = req.getParameter("extensions");
		logger.trace("File Browser Request. Path {} Check {} Extensions {} Workspace {}",
				new Object[] { path, check, extensionSet, workspace });
		
		boolean isWorkspaceMode = workspace != null && !workspace.isEmpty();

		if (path != null && !path.isEmpty()) {
			File f = new File(path);
			if (check != null) {
				PrintWriter writer = resp.getWriter();
				writer.println(validFile(f, extensionSet));
				writer.close();
				return;
			}
			ArrayList<FileEntry> files = new ArrayList<FileEntry>();
			ArrayList<FileEntry> dirs = new ArrayList<FileEntry>();

			if (!f.isDirectory()) {
				f = f.getParentFile();
			}

			FileFilter filter = makeFilter(extensionSet);
			File[] listFiles = f.listFiles(filter);
			if (f.getParentFile() != null
					&& !(isWorkspaceMode && !f.getParent().toString()
							.startsWith(workspace))) {
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
				String pathStr = isWorkspaceMode ? "path=" + workspace
						+ "&workspace=" + workspace : "path=" + this.userdir;
				resp.sendRedirect("/files/?" + pathStr + "&extensions="
						+ extensionSet);
			}
		}
	}

	public static boolean validFile(File f, String extensionset) {
		return f.exists()
				&& f.isFile()
				&& f.canRead()
				&& EXTENSIONS.get(extensionset) != null
				&& EXTENSIONS.get(extensionset).contains(
						Files.getFileExtension(f.getAbsolutePath()));
	}

	private FileFilter makeFilter(final String extensions) {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| (EXTENSIONS.containsKey(extensions) && EXTENSIONS
								.get(extensions).contains(
										Files.getFileExtension(f.getName())));
			}
		};
	}

}
