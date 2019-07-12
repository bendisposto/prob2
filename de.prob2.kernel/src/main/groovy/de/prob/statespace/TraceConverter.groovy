package de.prob.statespace

import de.prob.model.representation.AbstractModel

class TraceConverter {
	def static File save(Trace trace, String fileName) {
		final StringBuilder sb = new StringBuilder();
		def file = new File(fileName);
		file.newWriter()

		sb.append("<trace>")
		trace.getOpList(true).each { op ->
			if(op != null) {
				sb.append("<Operation name=\"")
				sb.append(op.getName())
				sb.append("\">")
				op.getParams().each { param ->
					sb.append("<Parameter name=\"")
					sb.append(param.toString())
					sb.append("\"/>")
				}
				sb.append("</Operation>\n")
			}
		}
		sb.append("</trace>\n")
		file.setText(sb.toString())
		file
	}

	def static File xmlToGroovy(String xmlFile, String outFile) {
		def file = new File(outFile)
		file.newWriter()

		file << '''{ m ->
	def next = { h, hash, name, args, strict ->
		def s = h as StateSpace
		def oldh = h
		def ns = s.getOutEdges(h.getCurrentState())
		def n = (ns.grep {it.sha() == hash})
		if (n.isEmpty()) {
			if (strict) {
				assert false, 'Could not replay exact trace.'
			} else {
				println "Warning: Cannot find precise solution for nondeterministic assignments"
				h = h.add(name,args)
			}
		} else {
			h = h.add(n.first().id);
		}
		assert h != null, 'Could not find a sucessor state. Trace so far is ${oldh}. Missing successor state for ${name} with arguments ${args}'
		h
	}

	def h = m as Trace
'''

		def trace = new XmlSlurper().parse(xmlFile)

		trace.Operation.each {
			def sha = it.sha.getAt(0).@value.toString()
			def params = []
			it.Parameter.each { params << "\"${it.@name}\"" }
			def name = "${it.@name}"
			if(name.startsWith("\$")) {
				name = "\\${name}"
			}
			file << "    h = next(h,\"${sha}\",\"${name}\",${params},true)\n"
		}

		file << "h  }"
	}

	def static Trace restore(AbstractModel model,String fileName) {
		def Trace t = model as Trace
		def StateSpace s = t as StateSpace

		def trace = new XmlSlurper().parse(fileName)

		trace.Operation.each {
			def sha = it.sha.getAt(0).@value.toString()
			def ops = s.getOutEdges(t.getCurrentState())
			def op = null
			ops.each {
				def state = s.getDest(it)
				if(state.hash == sha) {
					op = it
				}
			}

			if(op == null) {
				def params = []
				it.Parameter.each { params << "${it.@name}" }
				def name = "${it.@name}"
				t = t.add(name, params)
			} else {
				t = t.add(op.id)
			}
		}

		t
	}



	def static generateSpockTests(def directory) {
		def traceFiles = []
		new File(directory).eachFile(groovy.io.FileType.FILES) {
			if(it.name.endsWith(".trace")) {
				traceFiles << it
			}
		}
		StringBuilder sb = new StringBuilder()
		sb.append("""import de.prob.statespace.ReplayTraceTest

class TestRunner extends ReplayTraceTest {

""");
		traceFiles.each {
			sb.append("""    def "trace from ${it.name} can be replayed"(){
		when:
		def state = interpretLine(line)

		then:
		state != null

		where:
		line << extractLines("${it.getAbsolutePath()}")
	}

""")
		}

		sb.append("""}

new TestRunner()""")

		def testFile = new File(directory + "testRunner.groovy")
		if (!testFile.exists()) {
			testFile.createNewFile()
		}
		testFile.setText(sb.toString())
	}
}
