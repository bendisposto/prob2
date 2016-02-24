package de.prob.scripting

import static java.io.File.*

import com.google.common.io.Resources;
import com.google.inject.Inject

import de.prob.cli.OsSpecificInfo
import de.prob.cli.ProBInstanceProvider

import java.util.jar.JarEntry
import java.util.jar.JarFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

public class Installer {

	public static final String DEFAULT_HOME = System.getProperty("user.home") + File.separator+".prob"+File.separator+"prob2"+File.separator

	Logger logger = LoggerFactory.getLogger(Installer.class)

	def OsSpecificInfo osInfo
	def ProBInstanceProvider instances

	public Installer(final OsSpecificInfo osInfo) {
		this.osInfo = osInfo
		this.instances = instances
	}

	def ensureCLIsInstalled() {
		if (System.getProperty("prob.home")) {
			logger.info("prob.home is set. Not installing new CLI.")
			return "-----"
		}
		new FileHandler().defineUnzip()

		def dir = DEFAULT_HOME
		try {
			logger.info("Attempting to install CLI binaries")
			def os = osInfo.dirName
			def zipName = "probcli_${os}.zip"
			def zipPath = dir + zipName
			File zip = getResource("cli/"+zipName, zipPath)
			zip.unzip(dir)
			new File(zipPath).delete()

			def cspmf = os
			def outcspmf = dir + "lib"+ File.separator+"cspmf"
			if(os.startsWith('win')) {
				def zipFile = os == "win32" ? "windowslib32.zip" : "windowslib64.zip"
				zipPath = dir + zipFile
				zip = getResource("cli/"+zipFile, zipPath)
				zip.unzip(dir)
				cspmf = "windows.exe"
				outcspmf = outcspmf+".exe"
			}
			cspmf = "cspmf-"+cspmf
			def f = getResource("cli/"+cspmf,outcspmf)
			f.setExecutable(true)
			logger.info("CLI binaries successfully installed")
		} catch(IOException e) {
			logger.info("Exception $e occurred when trying to access resources.")
		}
	}

	def File getResource(String resource, String outFile) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(resource)
		File file = new File(outFile)
		file.append(is)
		file
	}
}