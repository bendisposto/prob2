package de.prob.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;

import com.google.common.io.ByteStreams;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.Main;
import de.prob.cli.OsSpecificInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public final class Installer {
	public static final Path DEFAULT_HOME = Paths.get(System.getProperty("user.home"), ".prob", "prob2-" + Main.getVersion());
	private static final Path LOCK_FILE_PATH = DEFAULT_HOME.resolve("installer.lock");
	private static final Logger logger = LoggerFactory.getLogger(Installer.class);
	private final OsSpecificInfo osInfo;

	@Inject
	private Installer(final OsSpecificInfo osInfo) {
		this.osInfo = osInfo;
	}

	@SuppressWarnings("try") // don't warn about unused resource in try
	public void ensureCLIsInstalled() {
		if (System.getProperty("prob.home") != null) {
			logger.info("prob.home is set. Not installing new CLI.");
			return;
		}

		logger.info("Attempting to install CLI binaries");
		try (
			final FileChannel lockFileChannel = FileChannel.open(LOCK_FILE_PATH, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			final FileLock lock = lockFileChannel.lock();
		) {
			logger.debug("Acquired installer lock file");
			final String os = osInfo.getDirName();
			try (final InputStream is = this.getClass().getResourceAsStream("/cli/probcli_" + os + ".zip")) {
				FileHandler.extractZip(is, DEFAULT_HOME);
			}

			final Path outcspmf;
			final String cspmfName;
			if (os.startsWith("win")) {
				final String bits = "win32".equals(os) ? "32" : "64";
				try (final InputStream is = this.getClass().getResourceAsStream("/cli/windowslib" + bits + ".zip")) {
					FileHandler.extractZip(is, DEFAULT_HOME);
				}
				outcspmf = DEFAULT_HOME.resolve("lib").resolve("cspmf.exe");
				cspmfName = "windows-cspmf.exe";
			} else {
				outcspmf = DEFAULT_HOME.resolve("lib").resolve("cspmf");
				cspmfName = os + "-cspmf";
			}

			try (
				final InputStream is = this.getClass().getResourceAsStream("/cli/" + cspmfName);
				final OutputStream fos = Files.newOutputStream(outcspmf);
			) {
				ByteStreams.copy(is, fos);
			}
			// Try to make the cspmf binary executable.
			final PosixFileAttributeView view = Files.getFileAttributeView(outcspmf, PosixFileAttributeView.class);
			if (view == null) {
				// If the PosixFileAttributeView is not available, we're probably on Windows, so nothing needs to be done
				logger.info("Could not get POSIX attribute view for cspmf binary (this is usually not an error)");
			} else {
				try {
					view.setPermissions(Collections.singleton(PosixFilePermission.OWNER_EXECUTE));
				} catch (UnsupportedOperationException e) {
					// If making the file executable is unsupported, we're probably on Windows, so nothing needs to be done
					logger.info("cspmf binary could not be made executable (this is usually not an error)", e);
				}
			}
			logger.info("CLI binaries successfully installed");
		} catch (IOException e) {
			logger.info("Exception occurred when trying to access resources.", e);
		}
	}
}
