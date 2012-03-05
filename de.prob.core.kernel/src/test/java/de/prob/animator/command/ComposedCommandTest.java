package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class ComposedCommandTest {

	private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	@Test
	public void testWriteCommand() throws ProBException {
		ICommand foo = mock(ICommand.class);
		ICommand bar = mock(ICommand.class);
		ComposedCommand cmd = new ComposedCommand(foo, bar);
		cmd.writeCommand(mock(IPrologTermOutput.class));
		verify(foo).writeCommand(any(IPrologTermOutput.class));
		verify(bar).writeCommand(any(IPrologTermOutput.class));
	}

	@Test
	public void testWriteCommandAlternateConstructor() throws ProBException {
		ICommand foo = mock(ICommand.class);
		ICommand bar = mock(ICommand.class);
		ArrayList<ICommand> list = new ArrayList<ICommand>();
		list.add(foo);
		list.add(bar);
		ComposedCommand cmd = new ComposedCommand(list);
		cmd.writeCommand(mock(IPrologTermOutput.class));
		verify(foo).writeCommand(any(IPrologTermOutput.class));
		verify(bar).writeCommand(any(IPrologTermOutput.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testProcessResult() throws ProBException {
		ICommand foo = mock(ICommand.class);
		ICommand bar = mock(ICommand.class);
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		ComposedCommand cmd = new ComposedCommand(foo, bar);
		cmd.processResult(map);
		verify(foo).processResult(any(ISimplifiedROMap.class));
		verify(bar).processResult(any(ISimplifiedROMap.class));
	}

	@Test
	public void testPrefix() throws Exception {
		ComposedCommand command = new ComposedCommand();
		int i = 0;
		char[] letters = LETTERS;
		for (char c : letters) {
			String prefix = command.createPrefix(i);
			assertEquals(prefix.charAt(0), c);
			assertEquals(1, prefix.length());
			i++;
		}
	}

	@Test
	public void testMorePrefix() throws Exception {
		ComposedCommand command = new ComposedCommand();
		char[] letters = LETTERS;
		int i = letters.length;
		for (char c : letters) {
			String prefix = command.createPrefix(i);
			assertEquals(prefix.charAt(0), c);
			assertEquals(prefix.charAt(1), '1');
			assertEquals(2, prefix.length());
			i++;
		}
	}
}
