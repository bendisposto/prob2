package de.prob.scripting

import groovy.json.JsonOutput
import groovy.json.JsonSlurper


public class ModelDir {

	JsonSlurper slurper = new JsonSlurper()
	JsonOutput writer = new JsonOutput()
	File modelFile
	File dir
	def files = [:]

	def ModelDir(File modelFile) {
		this.modelFile = modelFile
		def path = modelFile.absolutePath
		path = path.substring(0, path.lastIndexOf("."))
		dir = new File(path)
		dir.mkdir()
	}

	def String getFileText(String fileName) {
		getFile(fileName).getText()
	}

	/**
	 * Attempts to use {@link JsonSlurper#parseText(String)} to translate
	 * the specified file. Calls method {@link ModelDir#getFileText}.
	 * If translation fails, an exception may be thrown.
	 * @param fileName where the serialized information is stored
	 * @return The translated Object from
	 */
	def getContent(String fileName) {
		slurper.parseText(getFileText(fileName))
	}

	def setFileText(String fileName, String text) {
		getFile(fileName).setText(text)
	}

	def setContent(String fileName, content) {
		setFileText(fileName, writer.toJson(content))
	}

	def File getFile(String fileName) {
		if(files.containsKey(fileName)) {
			return files[fileName]
		}
		def f = FileHandler.getFile("${dir.absolutePath}${File.separator}$fileName")
		f.createNewFile()
		files[fileName] = f
		return f
	}

	def boolean equals(Object obj) {
		if(obj instanceof ModelDir) {
			return modelFile.absolutePath == ((ModelDir) obj).modelFile.absolutePath
		}
		false
	}

	def int hashCode() {
		modelFile.absolutePath.hashCode()
	}
}
