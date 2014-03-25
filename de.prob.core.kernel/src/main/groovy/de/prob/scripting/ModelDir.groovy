package de.prob.scripting



class ModelDir {

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
		getFile(fileName) << text
	}

	def clearFile(String fileName) {
		getFile(fileName).setText("")
	}

	def setFileText(String fileName, String text) {
		getFile(fileName).setText(text)
	}

	def File getFile(String fileName) {
		if(files.contains(fileName)) {
			return files[fileName]
		}
		def f = new File("${dir.absolutePath}/$fileName")
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
