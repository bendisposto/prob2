package de.prob.model.classicalb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.Ast2String;
import test.PolySuite;
import test.PolySuite.Config;
import test.PolySuite.Configuration;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;

@RunWith(PolySuite.class)
public class PrettyPredicatePrinterTest {

	private static final String PREFIX = "#PREDICATE ";

	static final String[] tests = { "x<y", "a<b&b<c", "x=y", "x:NAT", "A<:B",
			"A<<:B", "A/<:B", "A/<<:B", "x/=y", "x/:NAT", "x<=y", "x>y",
			"x>=y", "!X,Y.(X:NAT&Y:NAT=>x<y)", "#X,Y.(X:NAT&Y:NAT=>x<y)",
			"1=4 or 12=19", "1=4 => 12=19", "1=4 <=> 12=19", "not(7=3)" };

	// , "A=>B", "A or B", "A<=>B",

	String theString;

	public PrettyPredicatePrinterTest(String theString) {
		this.theString = theString;
	}

	@Test
	public void testExpression() throws Exception {
		Start parse = BParser.parse(PREFIX + theString);
		PrettyPrinter prettyprinter = new PrettyPrinter();
		parse.apply(prettyprinter);
		String prettyPrint = prettyprinter.getPrettyPrint();
		Start parse2 = BParser.parse(PREFIX + prettyPrint);
		PrettyPrinter prettyprinter2 = new PrettyPrinter();
		parse2.apply(prettyprinter2);

		assertEquals(Ast2String.getTreeAsString(parse), Ast2String.getTreeAsString(parse2));
		assertEquals(prettyPrint, prettyprinter2.getPrettyPrint());
	}

	@Config
	public static Configuration getConfig() {

		return new Configuration() {

			@Override
			public int size() {
				return tests.length;
			}

			@Override
			public String getTestValue(int index) {
				return tests[index];
			}

			@Override
			public String getTestName(int index) {
				return tests[index];
			}
		};
	}

}
