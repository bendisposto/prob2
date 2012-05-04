package de.prob.model.classicalb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.Ast2String;
import test.PolySuite;
import test.PolySuite.Config;
import test.PolySuite.Configuration;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;

@RunWith(PolySuite.class)
public class PrettyPredicatePrinterTest {

	static final String[] tests = { "x<y", "a<b&b<c", "x=y", "x:NAT", "A<:B",
			"A<<:B", "A/<:B", "A/<<:B", "x/=y", "x/:NAT", "x<=y", "x>y",
			"x>=y", "!X,Y.(X:NAT&Y:NAT=>x<y)", "#X,Y.(X:NAT&Y:NAT=>x<y)",
			"1=4 or 12=19", "1=4 => 12=19", "1=4 <=> 12=19" };

	// , "A=>B", "A or B", "A<=>B",

	String theString;

	public PrettyPredicatePrinterTest(String theString) {
		this.theString = theString;
	}

	@Test
	public void testExpression() throws Exception {
		String toParse = "#PREDICATE " + theString;
		Start parse = BParser.parse(toParse);
		PrettyPrinter prettyprinter = new PrettyPrinter();
		parse.apply(prettyprinter);
		String prettyPrint = prettyprinter.getPrettyPrint();
		Start parse2 = BParser.parse("#PREDICATE " + prettyPrint);
		PrettyPrinter prettyprinter2 = new PrettyPrinter();
		parse2.apply(prettyprinter2);

		assertEquals(getTreeAsString(parse), getTreeAsString(parse2));
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

	private String getTreeAsString(final Start startNode) throws BException {
		final Ast2String ast2String = new Ast2String();
		startNode.apply(ast2String);
		return ast2String.toString();
	}

}
