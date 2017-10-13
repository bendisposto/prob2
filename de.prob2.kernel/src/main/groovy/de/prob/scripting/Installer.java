package de.prob.scripting;

import de.prob.Main;
import de.prob.cli.OsSpecificInfo;
import de.prob.cli.ProBInstanceProvider;
import groovy.lang.GString;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Installer {
	public Installer(final OsSpecificInfo osInfo) {
		this.osInfo = osInfo;
		this.instances = instances;
	}

	public String ensureCLIsInstalled() {
		String s = System.getProperty("prob.home");
		if(s != null) {
			logger.info("prob.home is set. Not installing new CLI.");
			return "-----";
		}

		new FileHandler().defineUnzip();

		String dir = DEFAULT_HOME;
		try {
			logger.info("Attempting to install CLI binaries");
			final String os = osInfo.getDirName();
			String zipName = "probcli_" + os + ".zip";
			String zipPath = dir + zipName;
			File zip = getResource("cli/" + zipName, zipPath);
			DefaultGroovyMethods.invokeMethod(zip, "unzip", new Object[]{dir});
			new File(zipPath).delete();

			String outcspmf = dir + "lib" + File.separator + "cspmf";
			String cspmf = os + "-cspmf";
			if (os.startsWith("win")) {
				String zipFile = os.equals("win32") ? "windowslib32.zip" : "windowslib64.zip";
				zipPath = dir + zipFile;
				zip = getResource("cli/" + zipFile, zipPath);
				DefaultGroovyMethods.invokeMethod(zip, "unzip", new Object[]{dir});
				cspmf = "windows-cspmf.exe";
				outcspmf += ".exe";
			}

			File f = getResource("cli/" + cspmf, outcspmf);
			f.setExecutable(true);
			logger.info("CLI binaries successfully installed");
		} catch (IOException e) {
			logger.info("Exception " + String.valueOf(e) + " occurred when trying to access resources.");
		}
		return null;
	}

	public File getResource(String resource, String outFile) throws IOException{
		InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
		File file = new File(outFile);
		OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
		IOGroovyMethods.leftShift(os, is);
		((BufferedOutputStream) os).close();
		return file;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public OsSpecificInfo getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(OsSpecificInfo osInfo) {
		this.osInfo = osInfo;
	}

	public ProBInstanceProvider getInstances() {
		return instances;
	}

	public void setInstances(ProBInstanceProvider instances) {
		this.instances = instances;
	}

	public static final String DEFAULT_HOME = System.getProperty("user.home") + File.separator + ".prob" + File.separator + "prob2-" + Main.getVersion() + File.separator;
	private Logger logger = LoggerFactory.getLogger(Installer.class);
	private OsSpecificInfo osInfo;
	private ProBInstanceProvider instances;
}
