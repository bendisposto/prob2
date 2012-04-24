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

	def downloadCli(final String probhome) throws ProBException{
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


		// download config
		def configurl = "http://nightly.cobra.cs.uni-duesseldorf.de/tmp/config.groovy"
		def file = probhome + "config.groovy"
		download(configurl, file)
		File g = new File(file)

		// get location of desired cli version
		def config = new ConfigSlurper().parse(g.toURL())
		def versionurl = config."latest".url
		//def versionurl = config."1.3.4-test".url
		g.delete();


		// Use operating system to download the correct zip file
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
	}
}