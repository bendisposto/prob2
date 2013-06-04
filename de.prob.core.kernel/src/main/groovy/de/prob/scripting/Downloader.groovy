package de.prob.scripting

import static java.io.File.*

import java.util.zip.ZipInputStream

import com.google.inject.Inject

import de.prob.annotations.Home
import de.prob.cli.OsSpecificInfo
import de.prob.webconsole.GroovyExecution
import de.prob.webconsole.shellcommands.AbstractShellCommand


class Downloader extends AbstractShellCommand {

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
//		try {
//			def configurl = "http://nightly.cobra.cs.uni-duesseldorf.de/tmp/config.groovy"
//			def file = probhome + "config.groovy"
//			download(configurl, file)
//			File g = new File(file)
//			def config = new ConfigSlurper().parse(g.toURI().toURL())
//			g.delete()
//			return config
//		}
//		catch (Exception e) {
			return []
//		}
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

		if(os == "win32") {
			def target = probhome+"lib.zip";
			download(versionurl+"windowslib.zip",target)
			File r = new File(target)
			r.unzip(probhome)
			r.delete()
		}

		return "--Upgrade to version: ${targetVersion} (${url})  successful.--"
	}

	def installCSPM() {
		def target = probhome+"lib"+File.separator+"cspmf"
		def dirName = osInfo.dirName
		if(dirName == "win32") {
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
		if(dirName == "win32") {
			targetName += "windows"
		}
		download("http://nightly.cobra.cs.uni-duesseldorf.de/cspmf/"+targetName,target)
		new File(target).setExecutable(true)

		return "--CSP Parser cspmf upgraded to latest copy--"
	}


	@Override
	public List<String> complete(List<String> m, int pos) {
		def suggestions = m.isEmpty() ? availableVersions(): availableVersions().findAll { it.startsWith(m[0]) }
		if (suggestions.size() == 1) {
			return ["upgrade "+suggestions[0]]
		}
		else {
			return suggestions
		}
	}

	@Override
	public Object perform(List<String> m, GroovyExecution exec)
	throws IOException {
		if (config.isEmpty()) {
			config = downloadConfig()
		}
		if (m.size() < 2) {
			return listVersions()
		}
		def version = m[1]
		if(version == "cspm") {
			return installCSPM();
		}
		if (!((List) availableVersions()).contains(version)) {
			return "unknown version"
		}
		return downloadCli(version);
	}
}