package de.prob.scripting

import static java.io.File.*

import java.util.zip.ZipInputStream

import com.google.inject.Inject

import de.prob.annotations.Home
import de.prob.cli.OsSpecificInfo


class Downloader {

	def OsSpecificInfo osInfo
	def String probhome
	def config = downloadConfig()

	@Inject
	public Downloader(final OsSpecificInfo osInfo, @Home final String probhome) {
		this.osInfo = osInfo
		this.probhome = probhome
	}

	def download(address,target) {
		def file = new FileOutputStream(target)
		def out = new BufferedOutputStream(file)
		out << new URL(address).openStream()
		out.close()
	}

	/**
	 * Finds the valid versions that are available for download
	 * @return
	 */
	def ConfigObject downloadConfig() {
		// download config
		def configurl = "http://nightly.cobra.cs.uni-duesseldorf.de/tmp/config.groovy"
		def file = probhome + "config.groovy"
		download(configurl, file)
		File g = new File(file)
		def config = new ConfigSlurper().parse(g.toURI().toURL())
		g.delete()
		return config
	}

	def availableVersions() {
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


		// Use operating system to download the correct zip file
		def os = osInfo.dirName
		def targetzip = probhome+"probcli_${os}.zip"
		def url = versionurl + "probcli_${os}.zip"
		download(url,targetzip)

		// Unzip file to correct directory (probhome)
		File.metaClass.unzip = { String dest ->
			//in metaclass added methods, 'delegate' is the object on which
			//the method is called. Here it's the file to unzip
			def result = new ZipInputStream(new FileInputStream(delegate))
			def destFile = new File(dest)
			if(!destFile.exists()){
				destFile.mkdir();
			}
			result.withStream{
				def entry
				while(entry = result.nextEntry){
					if (!entry.isDirectory()){
						new File(dest + File.separator + entry.name).parentFile?.mkdirs()
						def output = new FileOutputStream(dest + File.separator
								+ entry.name)
						output.withStream{
							int len = 0;
							byte[] buffer = new byte[4096]
							while ((len = result.read(buffer)) > 0){
								output.write(buffer, 0, len);
							}
						}
					}
					else {
						new File(dest + File.separator + entry.name).mkdir()
					}
				}
			}

		}

		File f = new File(targetzip)
		f.unzip(probhome)
		f.delete()

		return "--Upgrade to version: ${targetVersion} (${url})  successful.--"
	}
}