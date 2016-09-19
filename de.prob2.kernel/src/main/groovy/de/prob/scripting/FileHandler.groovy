package de.prob.scripting

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.zip.ZipInputStream



/**
 * Provides helper methods for handling files. Allows the reading/writing of files and the
 * deserialization and serialization of JSON objects.
 *
 * @author joy
 *
 */
class FileHandler {
	private JsonSlurper slurper = new JsonSlurper()
	private JsonOutput writer = new JsonOutput()
	private files = [:]

	/**
	 * Finds the specified file if it exists, or creates it if it does not exist
	 * @param fileName of desired File.
	 * @return File object specified by fileName
	 */
	def File getFile(String fileName) {
		if(files.containsKey(fileName)) {
			files[fileName]
		}
		def f = new File(fileName)
		f.createNewFile()
		files[fileName] = f
		f
	}

	/**
	 * Gets the text of the specified file. Calls method {@link FileHandler#getFile(String)}
	 * @param fileName of desired File
	 * @return String text contained in file
	 */
	def String getFileText(String fileName) {
		getFile(fileName).getText()
	}

	/**
	 * Attempts to use {@link JsonSlurper#parseText(String)} to translate
	 * the specified file. Calls method {@link FileHandler#getFileText(String)}.
	 * If translation fails, an exception may be thrown.
	 * @param fileName where the serialized information is stored
	 * @return The translated Object from the file.
	 */
	def getContent(String fileName) {
		slurper.parseText(getFileText(fileName))
	}

	/**
	 * Attempts to deserialize the content of the specified file into Map<String,String>
	 * format. If this is possible, the Map<String,String> is returned. Otherwise,
	 * null is returned. Calls {@link FileHandler#getContent(String)}
	 * @param fileName where the serialized information is stored
	 * @return the deserialized content casted to Map<String,String> if this is possible,
	 * or null if not
	 */
	def Map<String,String> getMapOfStrings(String fileName) {
		try {
			(Map<String,String>) getContent(fileName)
		} catch(Exception e) {
			null
		}
	}

	/**
	 * Attempts to deserialize the content of the specified file into List<String>
	 * format. If this is possible, the List<String> is returned. Otherwise,
	 * null is returned. Calls {@link FileHandler#getContent(String)}
	 * @param fileName where the serialized information is stored
	 * @return the deserialized content casted to List<String> if this is possible,
	 * or null if not
	 */
	def List<String> getListOfStrings(String fileName) {
		try {
			(List<String>) getContent(fileName)
		} catch(Exception e) {
			null
		}
	}

	/**
	 * Sets the text of a specified file. Calls {@link FileHandler#getFile(String)}
	 * @param fileName of the file where the information is to be stored
	 * @param text to be stored in the file
	 */
	def void setFileText(String fileName, String text) {
		getFile(fileName).setText(text)
	}

	/**
	 * Translates the specified content to JSON format via {@link JsonOutput#toJson(Object)}.
	 * Then saves the serialized format in the specified file.
	 * Calls {@link FileHandler#setFileText(String, String)}
	 * @param fileName of the file where the content is to be stored
	 * @param content to be serialized
	 */
	def void setContent(String fileName, content) {
		setFileText(fileName, writer.toJson(content))
	}

	def static defineUnzip() {
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
				while(entry = result.nextEntry){ // FIXME this seems a bit strange
					if (entry.isDirectory()){
						createDirectory(dest, entry)
					}
					else {
						writeFile(result,dest, entry)
					}
				}
			}
		}
	}

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

	def void extractZip(File zip, String targetDirPath) {
		defineUnzip()
		zip.unzip(targetDirPath)
	}

	def void extractZip(String pathToZip, String targetDirPath) {
		extractZip(new File(pathToZip), targetDirPath)
	}
}
