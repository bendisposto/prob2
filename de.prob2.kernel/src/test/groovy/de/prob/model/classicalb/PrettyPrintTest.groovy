package de.prob.model.classicalb

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.util.PrettyPrinter

import spock.lang.Specification 

class PrettyPrintTest extends Specification {
	/*
	 * TODO 
	 * If the prettyprinter inserts a whitespace, this test will fail.
	 * Thus, we should convert this test to a junit test and move it to the parser.
	 */
	def "test pretty printing"() {
		given:
		final ast = BParser.parse("#EXPRESSION" + a)
		final prettyprinter = new PrettyPrinter()
		prettyprinter.setup()

		when:
		ast.apply(prettyprinter)

		then:
		prettyprinter.prettyPrint == b

		where:
		a           | b
		8           | '8'
		"a+(b*c)"   | "a+b*c"
		"(a+b)+c"   | "a+b+c"
		"a+(b+c)"   | "a+(b+c)"
		"(a-b)-c"   | "a-b-c"
		"a-(b-c)"   | "a-(b-c)"
		"a**b**c"   | "a**b**c"
		"a**(b**c)" | "a**b**c"
		"(a**b)**c" | "(a**b)**c"
	}

	def "test pretty predicate printing"() {
		given:
		final ast = BParser.parse("#PREDICATE " + a)
		final prettyprinter = new PrettyPrinter()

		when:
		ast.apply(prettyprinter)

		then:
		prettyprinter.prettyPrint == b

		where:
		a                     | b
		"x=1 => y=2"          | "x=1 => y=2"
		"x=1 => y=2 => (z=3)" | "x=1 => y=2 => z=3"
		"x=1 => (y=2 => z=3)" | "x=1 => (y=2 => z=3)"
	}

	def "test pretty printing for sets"() {
		given:
		final toParse = '''
		MACHINE scheduler
		SETS
			PID = {PID1,PID2,PID3}
		END
		'''
		final ast = BParser.parse(toParse)
		final prettyprinter = new PrettyPrinter()
		final setDef = ast.PParseUnit.machineClauses[0].setDefinitions[0]

		when:
		setDef.apply(prettyprinter)

		then:
		prettyprinter.prettyPrint == "PID={PID1,PID2,PID3}"
	}
}


