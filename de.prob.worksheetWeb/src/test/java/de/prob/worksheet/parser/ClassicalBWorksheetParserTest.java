package de.prob.worksheet.parser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.prob.worksheet.api.evalStore.EvalStoreAPI;

public class ClassicalBWorksheetParserTest {
	SimpleConsoleParser cbwParser;

	@Before
	public void setUp() throws Exception {
		this.cbwParser = new SimpleConsoleParser();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void splitToExpressions1() {
		final String[] res = this.cbwParser
				.splitToExpressions("\r\n selectHistory(1)\ngetHistories()  \rload(\"test,dsystem.mch\",true)");
		Assert.assertArrayEquals(new String[] { "", "", "selectHistory(1)",
				"getHistories()", "load(\"test,dsystem.mch\",true)" }, res);

	}

	@Test
	public void expressionsToMethods() {
		final String[][] res = this.cbwParser
				.expressionsToMethods(new String[] { "", "selectHistory(1)",
						"getHistories()", "load(\"test,dsystem.mch\",true)" });
		Assert.assertArrayEquals(
				new String[][] { { "selectHistory", "1" }, { "getHistories" },
						{ "load", "\"test,dsystem.mch\"", "true" } }, res);
	}

	@Test
	public void methodsToEvalObjects1() throws NoSuchMethodException,
			SecurityException {
		final Object[] res = this.cbwParser
				.methodsToEvalObjects(new String[][] {
						{ "selectHistory", "1" }, { "getHistories" },
						{ "load", "\"test,dsystem.mch\"", "true" } });
		Assert.assertArrayEquals(
				new Object[] {
						EvalStoreAPI.class.getDeclaredMethod("selectHistory",
								String.class),
						EvalStoreAPI.class.getDeclaredMethod("getHistories"),
						EvalStoreAPI.class.getDeclaredMethod("load",
								String.class, String.class) }, res);
	}

	@Test
	public void parseApiMethodNoBrackets() {
		final String[] res = this.cbwParser.parseApiMethod("selectHistory");
		Assert.assertArrayEquals(new String[] { "selectHistory" }, res);
	}

	@Test
	public void parseApiMethodEmpty() {
		final String[] res = this.cbwParser.parseApiMethod("selectHistory()");
		Assert.assertArrayEquals(new String[] { "selectHistory" }, res);
	}

	@Test
	public void parseApiMethodSimple1() {
		final String[] res = this.cbwParser.parseApiMethod("selectHistory(1)");
		Assert.assertArrayEquals(new String[] { "selectHistory", "1" }, res);
	}

	@Test
	public void parseApiMethodSimple2() {
		final String[] res = this.cbwParser
				.parseApiMethod("selectHistory(arg1,arg2)");
		Assert.assertArrayEquals(
				new String[] { "selectHistory", "arg1", "arg2" }, res);
	}

	@Test
	public void parseApiMethodArray() {
		final String[] res = this.cbwParser
				.parseApiMethod("selectHistory({a,b,c})");
		Assert.assertArrayEquals(new String[] { "selectHistory", "{a,b,c}" },
				res);
	}

	@Test
	public void argSplitSimple() {
		final String[] res = this.cbwParser.splitArgs("machine1,0,stateId");
		Assert.assertArrayEquals(new String[] { "machine1", "0", "stateId" },
				res);
	}

	@Test
	public void argSplitArray1() {
		final String[] res = this.cbwParser
				.splitArgs("machine1,{1,2,3},stateId");
		Assert.assertArrayEquals(new String[] { "machine1", "{1,2,3}",
				"stateId" }, res);
	}

	@Test
	public void argSplitArray2() {
		final String[] res = this.cbwParser
				.splitArgs("machine1,(a,b,c),stateId");
		Assert.assertArrayEquals(new String[] { "machine1", "(a,b,c)",
				"stateId" }, res);
	}

	@Test
	public void argSplitArray3() {
		final String[] res = this.cbwParser
				.splitArgs("machine1,[a,0,c],stateId");
		Assert.assertArrayEquals(new String[] { "machine1", "[a,0,c]",
				"stateId" }, res);
	}

	@Test
	public void argSplitString1() {
		final String[] res = this.cbwParser
				.splitArgs("machine1,\"statespace2,5\",stateId");
		Assert.assertArrayEquals(new String[] { "machine1",
				"\"statespace2,5\"", "stateId" }, res);
	}

	@Test
	public void argSplitString2() {
		final String[] res = this.cbwParser
				.splitArgs("machine1,'statespace2,5',stateId");
		Assert.assertArrayEquals(new String[] { "machine1", "'statespace2,5'",
				"stateId" }, res);
	}

	@Test
	public void argSplitComplex() {
		final String[] res = this.cbwParser
				.splitArgs("machine1,statespace2,{1,2,3},(a,\"b\",c),[{a->'a,b'},{a->c},{c->d}]");
		Assert.assertArrayEquals(new String[] { "machine1", "statespace2",
				"{1,2,3}", "(a,\"b\",c)", "[{a->'a,b'},{a->c},{c->d}]" }, res);
	}

}
