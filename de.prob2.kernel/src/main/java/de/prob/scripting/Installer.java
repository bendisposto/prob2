package de.prob.scripting;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
			final String zipName = "probcli_" + os + ".zip";
			final File zip = new File(DEFAULT_HOME + zipName);
			copyResourceToFile("cli/" + zipName, zip);
			FileHandler.extractZip(zip, DEFAULT_HOME);
			Files.delete(zip.toPath());

			String outcspmf = DEFAULT_HOME + "lib" + File.separator + "cspmf";
			String cspmfName = os + "-cspmf";
			if (os.startsWith("win")) {
				final String windowsLibsZipName = "win32".equals(os) ? "windowslib32.zip" : "windowslib64.zip";
				final File windowsLibsZip = new File(DEFAULT_HOME + windowsLibsZipName);
				copyResourceToFile("cli/" + windowsLibsZipName, windowsLibsZip);
				FileHandler.extractZip(windowsLibsZip, DEFAULT_HOME);
				cspmfName = "windows-cspmf.exe";
				outcspmf += ".exe";
			}

			final File cspmfFile = new File(outcspmf);
			copyResourceToFile("cli/" + cspmfName, cspmfFile);
			if (!cspmfFile.setExecutable(true)) {
				logger.warn("Could not set the cspmf binary as executable");
			}
			logger.info("CLI binaries successfully installed");
		} catch (IOException e) {
			logger.info("Exception occurred when trying to access resources.", e);
		}
	}

	public void copyResourceToFile(String resource, File outFile) throws IOException {
		try (
			final InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
			final OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
		) {
			final byte[] buf = new byte[1024];
			while (true) {
				final int count = is.read(buf, 0, buf.length);
				if (count == -1) {
					break;
				}
				os.write(buf, 0, count);
			}
		}
	}
}
