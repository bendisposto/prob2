package de.prob.model.classicalb;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.PolySuite;
import test.PolySuite.Config;
import test.PolySuite.Configuration;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;

@RunWith(PolySuite.class)
public class PrettyPrinterTest {

	static final String[] tests = { "0" };

	String theString;

	public PrettyPrinterTest(String theString) {
		this.theString = theString;
	}
	
	@Test
	public void testExpression() throws Exception {
		String toParse = "#EXPRESSION "+theString;
		Start parse = BParser.parse(toParse);
		PrettyPrinter prettyprinter = new PrettyPrinter();
		parse.apply(prettyprinter);
		assertEquals(theString, prettyprinter.getPrettyPrint());
	}

	@Config
	public static Configuration getConfig() {

		return new Configuration() {

			public int size() {
				return tests.length;
			}

			public String getTestValue(int index) {
				return tests[index];
			}

			public String getTestName(int index) {
				return tests[index];
			}
		};
	}

}
