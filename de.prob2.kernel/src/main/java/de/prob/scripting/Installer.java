package de.prob.scripting;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.prob.Main;
import de.prob.cli.OsSpecificInfo;

import org.codehaus.groovy.runtime.IOGroovyMethods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Installer {
	public static final String DEFAULT_HOME = System.getProperty("user.home") + File.separator + ".prob" + File.separator + "prob2-" + Main.getVersion() + File.separator;
	private final Logger logger = LoggerFactory.getLogger(Installer.class);
	private final OsSpecificInfo osInfo;

	public Installer(final OsSpecificInfo osInfo) {
		this.osInfo = osInfo;
	}

	public void ensureCLIsInstalled() {
		if (System.getProperty("prob.home") != null) {
			logger.info("prob.home is set. Not installing new CLI.");
			return;
		}

		logger.info("Attempting to install CLI binaries");
		try {
			final String os = osInfo.getDirName();
			final String zipName = "probcli_" + os + ".zip";
			final File zip = new File(DEFAULT_HOME + zipName);
			copyResourceToFile("cli/" + zipName, zip);
			FileHandler.extractZip(zip, DEFAULT_HOME);
			zip.delete();

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
			cspmfFile.setExecutable(true);
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
			IOGroovyMethods.leftShift(os, is);
		}
	}
}
