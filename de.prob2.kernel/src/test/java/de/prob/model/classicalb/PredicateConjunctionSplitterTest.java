package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Start;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateConjunctionSplitterTest {

	@Test
	public void testSinglePredicate() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE a=1");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(1, splitter.getPredicates().size());
	}

	@Test
	public void testSimpleConjunction2() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE a=1 & b=2");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(2, splitter.getPredicates().size());
	}

	@Test
	public void testSimpleConjunction3() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE a=1 & b=2 & c=3");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(3, splitter.getPredicates().size());
	}

	@Test
	public void testSimpleConjunctionParens() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE a=1 & (b=2 & c=3)");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(3, splitter.getPredicates().size());
	}

	@Test
	public void testComplexPredicates1() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE #x.(x:NAT & x < 6)");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(1, splitter.getPredicates().size());
	}

	@Test
	public void testComplexPredicates2a() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE a<6 & #x.(x:NAT & x < 6)");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(2, splitter.getPredicates().size());
	}

	@Test
	public void testComplexPredicates2b() throws BCompoundException {
		Start parse = BParser.parse("#PREDICATE #x.(x:NAT & x < 6) & a<6 ");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(2, splitter.getPredicates().size());
	}

	@Test
	public void testComplexPredicates3() throws BCompoundException {
		Start parse = BParser
				.parse("#PREDICATE a=1 & ( b=1 => c = 1 & d=1) & e=1");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(3, splitter.getPredicates().size());
	}

}
