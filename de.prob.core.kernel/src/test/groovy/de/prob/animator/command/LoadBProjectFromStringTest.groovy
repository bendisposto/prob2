package de.prob.animator.command

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import spock.lang.Specification;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.BParser;
import de.prob.ProBException;

class LoadBProjectFromStringTest  extends Specification {
	
	def String testmachine
	def LoadBProjectFromStringCommand c
	
	def setup() {
		testmachine = """
		MACHINE SimplyStructure
		VARIABLES aa, b, Cc
		INVARIANT aa : NAT
		INITIALISATION aa:=1
		CONSTANTS dd, e, Ff
		PROPERTIES dd : NAT
		SETS GGG; Hhh; JJ = {dada, dudu, TUTUT}; iII; kkk = {LLL}
		END
	  """
		c = new LoadBProjectFromStringCommand(testmachine)
	}
	
	def "parsing results in the creation of an ast"() {
		expect:
		c.parseString(testmachine, new BParser()) instanceof Start == true		
	}
	
	def "test write command"() {
		setup:
		def prologTermOutput = new StructuredPrologOutput();
		c.writeCommand(prologTermOutput)
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		PrologTerm argument = t.getArgument(1);
		PrologTerm argument2 = t.getArgument(2);
		
		expect:
		next != null
		next instanceof CompoundPrologTerm == true
		"load_classical_b" == t.getFunctor()
		2 == t.getArity()
		argument.isList() == true
		argument2.isVariable() == true
	}
	
	def "test process result"() {
		setup:
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		when(map.get(anyString())).thenReturn(
			new ListPrologTerm(new CompoundPrologTerm("blah blah blah")));
		
		
		when:
		c.processResult(map);
		
		then:
		thrown ProBException
	}
	
	def "test process result for empty list"() {
		setup:
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		ListPrologTerm l = new ListPrologTerm();
		when(map.get(anyString())).thenReturn(l);
		c.processResult(map);
	}
	
	
}
