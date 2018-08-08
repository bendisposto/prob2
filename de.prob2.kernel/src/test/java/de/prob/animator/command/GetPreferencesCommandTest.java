package de.prob.animator.command;

import java.util.Collection;
import java.util.List;

import de.prob.animator.domainobjects.ProBPreference;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class GetPreferencesCommandTest {

	@Test
	public void testGetPreferences() {
		GetDefaultPreferencesCommand gpc = new GetDefaultPreferencesCommand();
		assertNull(gpc.getPreferences());
	}

	@Test
	public void testProcessResults() {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Prefs")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("preference",
						new CompoundPrologTerm("tinker"),
						new CompoundPrologTerm("tailor"),
						new CompoundPrologTerm("soldier"),
						new CompoundPrologTerm("sailor"),
						new CompoundPrologTerm("foo")), new CompoundPrologTerm(
						"preference", new CompoundPrologTerm("richman"),
						new CompoundPrologTerm("poorman"),
						new CompoundPrologTerm("beggarman"),
						new CompoundPrologTerm("thief"),
						new CompoundPrologTerm("bar"))));

		GetDefaultPreferencesCommand command = new GetDefaultPreferencesCommand();
		command.processResult(map);

		List<ProBPreference> prefs = command.getPreferences();

		assertEquals(2, prefs.size());

		ProBPreference pref1 = prefs.get(0);
		assertEquals("tinker", pref1.name);
		assertEquals("tailor", pref1.type.toString());
		assertEquals("soldier", pref1.description);
		assertEquals("sailor", pref1.category);
		assertEquals("foo", pref1.defaultValue);

		ProBPreference pref2 = prefs.get(1);
		assertEquals("richman", pref2.name);
		assertEquals("poorman", pref2.type.toString());
		assertEquals("beggarman", pref2.description);
		assertEquals("thief", pref2.category);
		assertEquals("bar", pref2.defaultValue);
	}

	@Test
	public void testWriteCommand() {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetDefaultPreferencesCommand command = new GetDefaultPreferencesCommand();
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();

		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm t = sentences.iterator().next();
		assertNotNull(t);
		assertTrue(t instanceof CompoundPrologTerm);
		assertEquals("list_all_eclipse_preferences", t.getFunctor());
		assertEquals(1, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isVariable());
	}

	@Test(expected = ResultParserException.class)
	public void testProcessResultsFail() {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("blah blah blah")));

		GetDefaultPreferencesCommand command = new GetDefaultPreferencesCommand();
		command.processResult(map);
	}

	@Test(expected = ResultParserException.class)
	public void testProcessResultsFail2() {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(
				new CompoundPrologTerm("blah blah blah"));

		GetDefaultPreferencesCommand command = new GetDefaultPreferencesCommand();
		command.processResult(map);
	}

}
