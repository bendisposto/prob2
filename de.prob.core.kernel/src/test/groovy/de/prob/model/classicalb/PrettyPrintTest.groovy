package de.prob.model.classicalb;


import spock.lang.Specification;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;
import spock.lang.Ignore


public class PrettyPrintTest extends Specification {

	def "test pretty printing"() {
		when:
		Start parse = BParser.parse("#EXPRESSION" + a);
		PrettyPrinter prettyprinter = new PrettyPrinter();
		prettyprinter.setup();
		parse.apply(prettyprinter);
		String prettyPrint = prettyprinter.getPrettyPrint();

		then:
		prettyPrint == b

		where:
		a 	|   b
		8   |   '8'
		"a+(b*c)" | "a+b*c"
		"(a+b)+c" | "a+b+c"
		"a+(b+c)" | "a+(b+c)"
		"(a-b)-c" | "a-b-c"
		"a-(b-c)" | "a-(b-c)"
		"a**b**c" | "a**b**c"
		"a**(b**c)" | "a**b**c"
		"(a**b)**c" | "(a**b)**c"
	}

	def "test pretty predicate printing"() {

		when:
		Start parse = BParser.parse("#PREDICATE " + a);
		PrettyPrinter prettyprinter = new PrettyPrinter();

		parse.apply(prettyprinter);
		String prettyPrint = prettyprinter.getPrettyPrint();

		then:
		prettyPrint == b

		where:
		a 	|   b
		"x=1 => y=2" | "x=1 => y=2"
		"x=1 => y=2 => (z=3)" | "x=1 => y=2 => z=3"
		"x=1 => (y=2 => z=3)" | "x=1 => (y=2 => z=3)"
	}

	//FIXME: This is a reproduction of the odd behavior of the parsing of the sets
	@Ignore
	def "test pretty printing for sets"() {
		when:
		String toParse = '''MACHINE scheduler
							SETS
								PID = {PID1,PID2,PID3}
							END'''
		Start parse = BParser.parse(toParse);
		PrettyPrinter prettyprinter = new PrettyPrinter();

		parse.apply(prettyprinter);
		String prettyPrint = prettyprinter.getPrettyPrint();


		then:
		prettyPrint == "PID={PID1,PID2,PID3}"
	}
}


