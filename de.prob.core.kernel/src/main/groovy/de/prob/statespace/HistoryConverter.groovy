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
		def trace = history as ArrayList
		trace.each {
			def op = it.edge
			if(op != null) {
				file << "<Operation name=\"${op.getName()}\">"
				op.getParams().each {
					param -> file << "<Parameter name=\"$param\"/>"
				}
				file << "<sha value=\"${it.dest.hash}\"/>"
				file << "</Operation>"
			}
		}
		file << "</trace>"

		file
	}

	def static History restore(AbstractModel model,String fileName) {
		def History h = model as History
		def StateSpace s = h as StateSpace

		def trace = new XmlSlurper().parse(fileName)

		trace.Operation.each {
			def sha = it.sha.getAt(0).@value.toString()
			def ops = s.outgoingEdgesOf(h.getCurrentState())
			def op = null
			ops.each {
				def state = s.getEdgeTarget(it)
				if(state.hash == sha) {
					op = it
				}
			}

			if(op == null) {
				def params = []
						it.Parameter.each {
					params << "${it.@name}"
				}
				def name = "${it.@name}"
				h = h.add("${it.@name}", params)
			} else {
				h = h.add(op.id)
			}
		}

		h
	}
}
