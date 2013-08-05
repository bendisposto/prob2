package de.prob;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarFileLoader extends URLClassLoader {
	public JarFileLoader(URL[] urls) {
		super(urls);
	}

	public void addFile(String path) throws MalformedURLException {
		String urlPath = "jar:file://" + path + "!/";
		addURL(new URL(urlPath));
	}

	public void addFolder(String path) throws MalformedURLException {
		File f = new File(path);
		if (!f.isDirectory())
			throw new IllegalArgumentException("Path must be a directory");
		File[] files = f.listFiles();
		for (File file : files) {
			String urlPath = "jar:file://" + file.getPath() + "!/";
			addURL(new URL(urlPath));
		}
	}

	public static void main(String args[]) {
		try {
			System.out.println("First attempt...");
			Class.forName("lottery.core");
		} catch (Exception ex) {
			System.out.println("Failed.");
		}

		try {
			URL urls[] = {};

			JarFileLoader cl = new JarFileLoader(urls);
			cl.addFolder("/Users/bendisposto/.prob/plugins/");
			System.out.println("Second attempt...");
			cl.loadClass("lottery.core");
			System.out.println("Success!");
		} catch (Exception ex) {
			System.out.println("Failed.");
			ex.printStackTrace();
		}
	}
}