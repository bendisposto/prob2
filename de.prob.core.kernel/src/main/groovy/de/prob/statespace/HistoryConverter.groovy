package de.prob.statespace


class HistoryConverter {
	def static File save(History history,String fileName) {
		HistoryElement current = history.current
		def opList = current.getOpList()

		def out = new File(fileName)
		out.newWriter()
		out << "{ m -> def h = new History(m)\n"

		opList.each {
			def params = []
			it.params.each {
				params.add("\"$it\"")
			}

			if(it.name.startsWith("\$")) {
				out << "h = h.add(\"\\${it.name}\",$params)\n"
			} else {
				out << "h = h.add(\"${it.name}\",$params)\n"
			}
		}
		out << "h }"
		out
	}
}
