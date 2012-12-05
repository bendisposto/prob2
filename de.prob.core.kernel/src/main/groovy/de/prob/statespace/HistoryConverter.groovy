package de.prob.statespace

import de.prob.model.representation.AbstractModel


class HistoryConverter {
	def static File save(History history,String fileName) {
		def file = new File(fileName);
		file.newWriter()

		file << "<!-- Model for this trace has the following graph:"
		def model = history as AbstractModel;
		file << "${model.toString()} -->"

		file << "<trace>"
		history.current.opList.each {
			file << "<Operation name=\"${it.getName()}\">"
			it.getParams().each {
				param -> file << "<Parameter name=\"$param\"/>"
			}
			file << "</Operation>"
		}
		file << "</trace>"

		file
	}

	def static History restore(AbstractModel model,String fileName) {
		def History history = model as History

		def trace = new XmlSlurper().parse(fileName)

		trace.Operation.each {
			def params = []
			it.Parameter.each {
				params << "${it.@name}"
			}
			def name = "${it.@name}"
			history = history.add("${it.@name}", params)
		}

		history
	}
}
