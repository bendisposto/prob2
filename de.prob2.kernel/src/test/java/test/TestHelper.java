package test;

import static org.mockito.Mockito.*;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class TestHelper {

	public static ISimplifiedROMap<String, PrologTerm> mkAtomMock(
			final String... strings) {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		for (int i = 0; i < strings.length; i += 2) {
			String key = strings[i];
			String value = strings[i + 1];
			when(map.get(key)).thenReturn(new CompoundPrologTerm(value));
		}

		return map;
	}
}
