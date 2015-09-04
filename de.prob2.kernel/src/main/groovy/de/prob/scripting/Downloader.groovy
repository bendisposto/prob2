package de.prob.scripting

import static java.io.File.*

import com.google.inject.Inject

import de.prob.annotations.Home
import de.prob.cli.OsSpecificInfo
import de.prob.cli.ProBInstanceProvider



class Downloader {

	def OsSpecificInfo osInfo
	def String probhome
	def config
	def ProBInstanceProvider instances

	@Inject
	public Downloader(final OsSpecificInfo osInfo, @Home final String probhome, ProBInstanceProvider instances) {
		this.osInfo = osInfo
		this.probhome = probhome
		this.config = downloadConfig()
		this.instances = instances
	}

	def download(address,target) {
		def file = new FileOutputStream(target)
		def out = new BufferedOutputStream(file)
		def conn = new URL(address).openConnection()
		conn.setReadTimeout(1000)
		out << conn.getInputStream()
		out.close()
	}

	/**
	 * Finds the valid versions that are available for download
	 * @return
	 */
	def ConfigObject downloadConfig() {
		// download config
		try {
			def configurl = "http://nightly.cobra.cs.uni-duesseldorf.de/tmp/config.groovy"
			def file = probhome + "config.groovy"
			download(configurl, file)
			File g = new File(file)
			def config = new ConfigSlurper().parse(g.toURI().toURL())
			g.delete()
			return config
		}
		catch (Exception e) {
			return []
		}
	}

	def  availableVersions() {
		if (config.isEmpty())
			config = downloadConfig();
		config.collect { it.getKey()}
	}


	/**
	 * Lists the possible versions of the ProB Cli that are available for download
	 * @return
	 */
	def String listVersions(){
		StringBuilder sb = new StringBuilder()
		sb.append("Possible Versions are:\n")
		config.each {
			sb.append("  ")
			sb.append(it.getKey())
			sb.append("\n")
		}
		return sb.toString()
	}

	def String downloadCli() {
		return listVersions();
	}

	/**
	 * Checks if the given targetVersion is valid. If it is, the targetVersion of
	 * the ProB Cli is downloaded, unzipped, and saved in probhome
	 *
	 * @param targetVersion
	 * @return
	 * @throws ProBException
	 */
	def String downloadCli(final String targetVersion) {
		def config = downloadConfig()
		if( !config.containsKey(targetVersion)) {
			return "There is no version available for version name <"+targetVersion+">\n"+listVersions()
		}
		def versionurl = config.get(targetVersion).url

		def numOfOpenCLIs = instances.numberOfCLIs()
		if (numOfOpenCLIs != 0) {
			return "--Cannot download the ProB binaries. Close the $numOfOpenCLIs CLIs that are open--"
		}

		// Use operating system to download the correct zip file
		def os = osInfo.dirName
		def zipName = os == "win64" ? "win32" : os
		def targetzip = probhome+"probcli_${zipName}.zip"
		def url = versionurl + "probcli_${zipName}.zip"
		download(url,targetzip)

		new FileHandler().extractZip(targetzip, probhome)
		new File(targetzip).delete()

		if(os == "win32" || os == "win64") {
			def target = probhome+"lib.zip";
			def zipFile = os == "win32" ? "windowslib32.zip" : "windowslib64.zip"

			download(versionurl+zipFile,target)
			File r = new File(target)
			r.unzip(probhome)
			r.delete()
		}

		return "--Upgrade to version: ${targetVersion} (${url})  successful.--"
	}

	def String installCSPM() {
		def target = probhome+"lib"+File.separator+"cspmf"
		def dirName = osInfo.dirName
		if(dirName == "win32" || dirName == "win64") {
			target += ".exe"
		}
		def File f = new File(target);

		def targetName = "cspmf-"
		if(dirName == "linux") {
			targetName += "linux32"
		}
		if(dirName == "linux64" || dirName == "leopard64") {
			targetName += dirName
		}
		if(dirName == "win32" || dirName == "win64") {
			targetName += "windows"
		}
		download("http://nightly.cobra.cs.uni-duesseldorf.de/cspmf/"+targetName,target)
		new File(target).setExecutable(true)

		return "--CSP Parser cspmf upgraded to latest copy--"
	}


}