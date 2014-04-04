package de.prob.scripting

import groovy.json.JsonOutput
import groovy.json.JsonSlurper



/**
 * Provides helper methods for handling files. Allows the reading/writing of files and the
 * deserialization and serialization of JSON objects.
 *
 * @author joy
 *
 */
class FileHandler {
	def static JsonSlurper slurper
	def static JsonOutput writer

	static {
		slurper = new JsonSlurper()
		writer = new JsonOutput()
	}

	/**
	 * Finds the specified file if it exists, or creates it if it does not exist
	 * @param fileName of desired File.
	 * @return File object specified by fileName
	 */
	def static File getFile(String fileName) {
		def f = new File(fileName)
		f.createNewFile()
		f
	}

	/**
	 * Gets the text of the specified file. Calls method {@link FileHandler#getFile(String)}
	 * @param fileName of desired File
	 * @return String text contained in file
	 */
	def static String getFileText(String fileName) {
		getFile(fileName).getText()
	}

	/**
	 * Attempts to use {@link JsonSlurper#parseText(String)} to translate
	 * the specified file. Calls method {@link FileHandler#getFileText(String)}.
	 * If translation fails, an exception may be thrown.
	 * @param fileName where the serialized information is stored
	 * @return The translated Object from the file.
	 */
	def static getContent(String fileName) {
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
	def static Map<String,String> getMapOfStrings(String fileName) {
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
	def static List<String> getListOfStrings(String fileName) {
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
	def static void setFileText(String fileName, String text) {
		getFile(fileName).setText(text)
	}

	/**
	 * Translates the specified content to JSON format via {@link JsonOutput#toJson(Object)}.
	 * Then saves the serialized format in the specified file.
	 * Calls {@link FileHandler#setFileText(String, String)}
	 * @param fileName of the file where the content is to be stored
	 * @param content to be serialized
	 */
	def static void setContent(String fileName, content) {
		setFileText(fileName, writer.toJson(content))
	}
}
