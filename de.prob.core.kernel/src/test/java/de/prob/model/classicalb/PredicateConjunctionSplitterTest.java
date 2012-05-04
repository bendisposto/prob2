package de.prob.model.classicalb;

import static org.junit.Assert.*;

import org.junit.Test;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;

public class PredicateConjunctionSplitterTest {

	@Test
	public void testSinglePredicate() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE a=1");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(1,splitter.getPredicates().size());
	}
	
	@Test
	public void testSimpleConjunction2() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE a=1 & b=2");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(2,splitter.getPredicates().size());
	}
	@Test
	public void testSimpleConjunction3() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE a=1 & b=2 & c=3");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(3,splitter.getPredicates().size());
	}
	@Test
	public void testSimpleConjunctionParens() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE a=1 & (b=2 & c=3)");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(3,splitter.getPredicates().size());
	}

	@Test
	public void testComplexPredicates1() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE #x.(x:NAT & x < 6)");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(1,splitter.getPredicates().size());
	}
	
	@Test
	public void testComplexPredicates2a() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE a<6 & #x.(x:NAT & x < 6)");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(2,splitter.getPredicates().size());
	}
	
	@Test
	public void testComplexPredicates2b() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE #x.(x:NAT & x < 6) & a<6 ");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(2,splitter.getPredicates().size());
	}
	
	@Test
	public void testComplexPredicates3() throws Exception {
		Start parse = BParser
				.parse("#PREDICATE a=1 & ( b=1 => c = 1 & d=1) & e=1");
		PredicateConjunctionSplitter splitter = new PredicateConjunctionSplitter();
		parse.apply(splitter);
		assertEquals(3,splitter.getPredicates().size());
	}

}
