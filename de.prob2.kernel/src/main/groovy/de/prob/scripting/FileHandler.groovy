package de.prob.scripting

import java.util.zip.ZipInputStream

/**
 * Provides helper methods for handling files.
 *
 * @author joy
 *
 */
class FileHandler {
	private static void createDirectory(String dest, java.util.zip.ZipEntry entry) {
		new File(dest + File.separator + entry.name).mkdir()
	}

	private static void writeFile(InputStream stream, String dest, java.util.zip.ZipEntry entry) {
		new File(dest + File.separator + entry.name).parentFile?.mkdirs()
		def output = new FileOutputStream(dest + File.separator
				+ entry.name)
		output.withStream{
			int len = 0;
			byte[] buffer = new byte[4096]
			while ((len = stream.read(buffer)) > 0){
				output.write(buffer, 0, len);
			}
		}
	}

	static void extractZip(File zip, String targetDirPath) {
		def destFile = new File(targetDirPath)
		if(!destFile.exists()){
			destFile.mkdir();
		}
		def inStream = new ZipInputStream(new FileInputStream(zip))
		inStream.withStream {
			def entry
			while (entry = inStream.nextEntry) { // null is falsey in Groovy! 
				if (entry.isDirectory()) {
					createDirectory(targetDirPath, entry)
				} else {
					writeFile(inStream, targetDirPath, entry)
				}
			}
		}
	}

	static void extractZip(String pathToZip, String targetDirPath) {
		extractZip(new File(pathToZip), targetDirPath)
	}
}
