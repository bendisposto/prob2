package de.prob.animator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.animator.domainobjects.ProBPreference;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetPreferencesCommandTest {

	@Test
	public void testGetPreferences() {
		GetPreferencesCommand gpc = new GetPreferencesCommand();
		assertEquals(gpc.getPreferences(), null);
	}

	@Test
	public void testProcessResults() throws ProBException {

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

		GetPreferencesCommand command = new GetPreferencesCommand();
		command.processResult(map);

		List<ProBPreference> prefs = command.getPreferences();

		assertEquals(prefs.size(), 2);

		ProBPreference pref1 = prefs.get(0);
		assertEquals(pref1.name, "tinker");
		assertEquals(pref1.type.toString(), "tailor");
		assertEquals(pref1.description, "soldier");
		assertEquals(pref1.category, "sailor");
		assertEquals(pref1.defaultValue, "foo");

		ProBPreference pref2 = prefs.get(1);
		assertEquals(pref2.name, "richman");
		assertEquals(pref2.type.toString(), "poorman");
		assertEquals(pref2.description, "beggarman");
		assertEquals(pref2.category, "thief");
		assertEquals(pref2.defaultValue, "bar");
	}

	@Test
	public void testWriteCommand() {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetPreferencesCommand command = new GetPreferencesCommand();
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();

		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm t = sentences.iterator().next();
		assertNotNull(t);
		assertTrue(t instanceof CompoundPrologTerm);
		assertEquals("list_eclipse_preferences", t.getFunctor());
		assertEquals(1, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isVariable());
	}

	@Test(expected = ProBException.class)
	public void testProcessResultsFail() throws ProBException {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("blah blah blah")));

		GetPreferencesCommand command = new GetPreferencesCommand();
		command.processResult(map);
	}

	@Test(expected = ProBException.class)
	public void testProcessResultsFail2() throws ProBException {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(
				new CompoundPrologTerm("blah blah blah"));

		GetPreferencesCommand command = new GetPreferencesCommand();
		command.processResult(map);
	}

}
