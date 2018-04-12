package de.prob.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

	public static void extractZip(final InputStream zipFileStream, final Path targetDir) throws IOException {
		Files.createDirectories(targetDir);

		try (final ZipInputStream inStream = new ZipInputStream(zipFileStream)) {
			ZipEntry entry;
			while ((entry = inStream.getNextEntry()) != null) {
				final Path dest = targetDir.resolve(entry.getName());
				if (dest.getParent() != null) {
					Files.createDirectories(dest.getParent());
				}
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
}
