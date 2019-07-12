package de.prob.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
					Files.copy(inStream, dest, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}
}
