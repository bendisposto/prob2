package de.prob.scripting

import static java.io.File.*;

import java.util.zip.ZipInputStream

import de.prob.ProBException



class Downloader {
	def download(address,target) {
		def file = new FileOutputStream(target)
		def out = new BufferedOutputStream(file)
		out << new URL(address).openStream()
		out.close()
	}

	def String chooseOS(){
		// Choose operating system
		String os = null;
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			os = "win32";
		} else if (osName.startsWith("Mac")) {
			os = "leopard";
		} else if (osName.equals("Linux")) {
			String osArch = System.getProperty("os.arch");
			if (osArch.equals("i386")) {
				os = "linux";
			} else if (osArch.equals("amd64")) {
				os = "linux64";
			}
		} else {
			throw new ProBException();
		}
	}

	def ConfigObject downloadConfig(final String probhome) {
		// download config
		def configurl = "http://nightly.cobra.cs.uni-duesseldorf.de/tmp/config.groovy"
		def file = probhome + "config.groovy"
		download(configurl, file)
		File g = new File(file)
		def config = new ConfigSlurper().parse(g.toURI().toURL())
		g.delete()
		return config
	}

	def String listVersions(probhome){
		StringBuilder sb = new StringBuilder()
		sb.append("Possible Versions are:\n")
		def config = downloadConfig(probhome)
		config.each {
			sb.append("  ")
			sb.append(it.getKey())
			sb.append("\n")}
		return sb.toString()
	}

	def String downloadCli(final String probhome, final String targetVersion) throws ProBException{
		def config = downloadConfig(probhome)
		if( !config.containsKey(targetVersion)) {
			return "There is no version available for version name <"+targetVersion+">\n"+listVersions()
		}
		def versionurl = config.get(targetVersion).url


		// Use operating system to download the correct zip file
		def os = chooseOS()
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