package de.prob.scripting

import static java.io.File.*;

import java.util.zip.ZipInputStream

import de.prob.ProBException
import de.prob.annotations.Home
import de.prob.cli.OsSpecificInfo;

import com.google.inject.Inject;


class Downloader {

	def OsSpecificInfo osInfo
	def String probhome

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

	def String listVersions(){
		StringBuilder sb = new StringBuilder()
		sb.append("Possible Versions are:\n")
		def config = downloadConfig()
		config.each {
			sb.append("  ")
			sb.append(it.getKey())
			sb.append("\n")
		}
		return sb.toString()
	}

	def String downloadCli(final String targetVersion) throws ProBException{
		def config = downloadConfig(probhome)
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

		return "--Upgrade to version: "+targetVersion+" successful.--"
	}
}