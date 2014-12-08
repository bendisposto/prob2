package de.prob.statespace;
import javax.script.ScriptEngine

import spock.lang.Shared
import spock.lang.Specification
import de.prob.Main
import de.prob.scripting.ScriptEngineProvider

public class ReplayTraceTest extends Specification {
	@Shared
	def ScriptEngine engine

	def setupSpec() {
		engine = Main.getInjector().getInstance(ScriptEngineProvider.class).get()
	}

	def extractLines(String filePath) {
		def lines = []
		new File(filePath).eachLine { lines << it }
		lines
	}

	def interpretLine(final String line) {
		def x
		if (line.startsWith("#")) {
			x = internalMethod(line[1..(line.size()-1)])
			if (!x) throw new IllegalArgumentException("Line $line is an invalid method")
		} else {
			try {
				x = engine.eval("sId = sId.$line")
			} catch(Exception e) {
				throw new IllegalArgumentException("Line $line invalid because ${e.getMessage()}")
			}
		}
		x
	}

	def internalMethod(line) {
		def x
		def split = line.split(" ")
		if (split.size() < 1) {
			return false
		}
		try {
			x = this.invokeMethod(split.first(),split.tail())
		} catch(Exception e) {
			return false
		}
		x
	}

	def load(formalism, fileName) {
		engine.eval("s = api.${formalism}_load('$fileName') as StateSpace; sId = s.getRoot()")
	}
}