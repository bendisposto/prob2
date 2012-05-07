package de.prob.model.classicalb;


import spock.lang.Specification;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;


public class PrettyPrintTest extends Specification {
	
	def "test pretty printing"() {

		when:
			Start parse = BParser.parse("#EXPRESSION" + a);
			PrettyPrinter prettyprinter = new PrettyPrinter();
			parse.apply(prettyprinter);
			String prettyPrint = prettyprinter.getPrettyPrint();

		then:
			prettyPrint == b

		where:
		a 	|   b
		8   |   '8'
	}
	

}


