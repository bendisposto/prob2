package de.prob.scripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.common.io.ByteStreams;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.Main;
import de.prob.cli.OsSpecificInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public final class Installer {
	public static final String DEFAULT_HOME = System.getProperty("user.home") + File.separator + ".prob" + File.separator + "prob2-" + Main.getVersion() + File.separator;
	private static final Path LOCK_FILE_PATH = Paths.get(DEFAULT_HOME, "installer.lock");
	private static final Logger logger = LoggerFactory.getLogger(Installer.class);
	private final OsSpecificInfo osInfo;

	@Inject
	private Installer(final OsSpecificInfo osInfo) {
		this.osInfo = osInfo;
	}

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
				FileHandler.extractZip(is, Paths.get(DEFAULT_HOME));
			}

			String outcspmf = DEFAULT_HOME + "lib" + File.separator + "cspmf";
			String cspmfName = os + "-cspmf";
			if (os.startsWith("win")) {
				final String bits = "win32".equals(os) ? "32" : "64";
				try (final InputStream is = this.getClass().getResourceAsStream("/cli/windowslib" + bits + ".zip")) {
					FileHandler.extractZip(is, Paths.get(DEFAULT_HOME));
				}
				cspmfName = "windows-cspmf.exe";
				outcspmf += ".exe";
			}

			try (
				final InputStream is = this.getClass().getResourceAsStream("/cli/" + cspmfName);
				final OutputStream fos = new FileOutputStream(outcspmf);
			) {
				ByteStreams.copy(is, fos);
			}
			final File cspmfFile = new File(outcspmf);
			if (!cspmfFile.setExecutable(true)) {
				logger.warn("Could not set the cspmf binary as executable");
			}
			logger.info("CLI binaries successfully installed");
		} catch (IOException e) {
			logger.info("Exception occurred when trying to access resources.", e);
		}
	}
}
