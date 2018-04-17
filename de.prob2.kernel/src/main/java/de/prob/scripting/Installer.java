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
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

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

	/**
	 * Fix permissions of the given path (set user read and write bits). This is needed to fix a previous mistake that cleared the owner read/write bits on the cspmf binary.
	 *
	 * @param path the path of the file to fix
	 */
	private static void fixPermissions(final Path path) throws IOException {
		try {
			final Set<PosixFilePermission> perms = new HashSet<>(Files.readAttributes(path, PosixFileAttributes.class).permissions());
			final PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class);
			if (view == null) {
				// If the PosixFileAttributeView is not available, we're probably on Windows, so nothing needs to be done
				logger.info("Could not get POSIX attribute view for {} (this is usually not an error)", path);
				return;
			}
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			view.setPermissions(perms);
		} catch (UnsupportedOperationException e) {
			// If POSIX attributes are unsupported, we're probably on Windows, so nothing needs to be done
			logger.info("Could not fix permissions of {} (this is usually not an error): {}", path, e);
		}
	}

	/**
	 * Set or clear the executable bits of the given path.
	 *
	 * @param path the path of the file to make (non-)executable
	 * @param executable whether the file should be executable
	 */
	private static void setExecutable(final Path path, final boolean executable) throws IOException {
		try {
			final Set<PosixFilePermission> perms = new HashSet<>(Files.readAttributes(path, PosixFileAttributes.class).permissions());
			final PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class);
			if (view == null) {
				// If the PosixFileAttributeView is not available, we're probably on Windows, so nothing needs to be done
				logger.info("Could not get POSIX attribute view for {} (this is usually not an error)", path);
				return;
			}
			if (executable) {
				perms.add(PosixFilePermission.OWNER_EXECUTE);
				perms.add(PosixFilePermission.GROUP_EXECUTE);
				perms.add(PosixFilePermission.OTHERS_EXECUTE);
			} else {
				perms.remove(PosixFilePermission.OWNER_EXECUTE);
				perms.remove(PosixFilePermission.GROUP_EXECUTE);
				perms.remove(PosixFilePermission.OTHERS_EXECUTE);
			}
			view.setPermissions(perms);
		} catch (UnsupportedOperationException e) {
			// If POSIX attributes are unsupported, we're probably on Windows, so nothing needs to be done
			logger.info("Could not set executable status of {} to {} (this is usually not an error): {}", path, executable, e);
		}
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

			fixPermissions(outcspmf);
			try (
				final InputStream is = this.getClass().getResourceAsStream("/cli/" + cspmfName);
				final OutputStream fos = Files.newOutputStream(outcspmf);
			) {
				ByteStreams.copy(is, fos);
			}
			setExecutable(outcspmf, true);
			logger.info("CLI binaries successfully installed");
		} catch (IOException e) {
			logger.info("Exception occurred when trying to access resources.", e);
		}
	}
}
