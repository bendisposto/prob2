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

	static final String[] tests = {
			"x<y",
			"a<b&b<c",
			"x=y",
			"x:NAT",
			"A<:B",
			"A<<:B",
			"A/<:B",
			"A/<<:B",
			"x/=y",
			"x/:NAT",
			"x<=y",
			"x>y",
			"x>=y",
			"!X,Y.(X:NAT&Y:NAT=>x<y)",
			"#X,Y.(X:NAT&Y:NAT=>x<y)",
			"1=4 or 12=19",
			"1=4 => 12=19",
			"1=4 <=> 12=19",
			"not(7=3)",
			"!y.(y:DOM => !(x1,x2).(x1:DOM & x1<x2 & x2:DOM  => (Board(x1)(y) /= Board(x2)(y) &	Board(y)(x1) /= Board(y)(x2))))&!(s1,s2).(s1:SUBSQ & s2:SUBSQ => !(x1,y1,x2,y2).( (x1:s1 & x2:s1 & x1>=x2 & (x1=x2 => y1>y2) & y1:s2 & y2:s2 & (x1,y1) /= (x2,y2)) => Board(x1)(y1) /= Board(x2)(y2)))",
			"!(i1,j1,i2,j2).(( i1>0 & i2>0 & j1<=n & j2 <= n & i1<j1 & i2<j2 & (i1,j1) /= (i2,j2) & i1<=i2 & (i1=i2 => j1<j2)) => (a(j1)-a(i1) /= a(j2)-a(i2)))",
			"x+1:NAT" };

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
		assertEquals(Ast2String.getTreeAsString(parse),
				Ast2String.getTreeAsString(parse2));
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
