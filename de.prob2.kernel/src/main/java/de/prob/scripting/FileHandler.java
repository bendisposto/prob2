package de.prob.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Provides helper methods for handling files.
 *
 * @author joy
 */
public class FileHandler {
	private static void createDirectory(String dest, ZipEntry entry) {
		new File(dest + File.separator + entry.getName()).mkdir();
	}

	private static void writeFile(final InputStream stream, String dest, ZipEntry entry) throws IOException {
		final File file = new File(dest + File.separator + entry.getName());
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}
		try (final FileOutputStream output = new FileOutputStream(file)) {
			int len;
			byte[] buffer = new byte[4096];
			while ((len = stream.read(buffer)) > 0) {
				output.write(buffer, 0, len);
			}
		}
	}

	public static void extractZip(File zip, final String targetDirPath) throws IOException {
		File destFile = new File(targetDirPath);
		destFile.mkdir();
		
		try (final ZipInputStream inStream = new ZipInputStream(new FileInputStream(zip))) {
			ZipEntry entry;
			while ((entry = inStream.getNextEntry()) != null) { 
				if (entry.isDirectory()) {
					createDirectory(targetDirPath, entry);
				} else {
					writeFile(inStream, targetDirPath, entry);
				}
			}
		}
	}

	public static void extractZip(String pathToZip, String targetDirPath) throws IOException {
		extractZip(new File(pathToZip), targetDirPath);
	}
}
