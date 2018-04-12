package de.prob.scripting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteStreams;

/**
 * Provides helper methods for handling files.
 *
 * @author joy
 */
public final class FileHandler {
	private FileHandler() {
		throw new AssertionError("Utility class");
	}

	public static void extractZip(final Path zip, final Path targetDir) throws IOException {
		Files.createDirectories(targetDir);

		try (final ZipInputStream inStream = new ZipInputStream(Files.newInputStream(zip))) {
			ZipEntry entry;
			while ((entry = inStream.getNextEntry()) != null) {
				final Path dest = targetDir.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(dest);
				} else {
					try (final OutputStream output = Files.newOutputStream(dest)) {
						ByteStreams.copy(inStream, output);
					}
				}
			}
		}
	}

	public static void extractZip(File zip, final String targetDirPath) throws IOException {
		extractZip(zip.toPath(), Paths.get(targetDirPath));
	}

	public static void extractZip(String pathToZip, String targetDirPath) throws IOException {
		extractZip(new File(pathToZip), targetDirPath);
	}
}
