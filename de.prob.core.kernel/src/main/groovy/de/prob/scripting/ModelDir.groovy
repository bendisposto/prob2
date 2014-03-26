package de.prob.scripting

public class ModelDir {

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

	def appendToFile(String fileName, String text) {
		getFile(fileName) << "$text\n"
	}

	def clearFile(String fileName) {
		getFile(fileName).setText("")
	}

	def setFileText(String fileName, String text) {
		getFile(fileName).setText(text)
	}

	def File getFile(String fileName) {
		if(files.containsKey(fileName)) {
			return files[fileName]
		}
		def f = new File("${dir.absolutePath}/$fileName")
		f.createNewFile()
		files[fileName] = f
		return f
	}

	def List<String> getLines(String fileName) {
		def list = []
		getFile(fileName).each { list << it }
		list
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
