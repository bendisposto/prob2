package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class ComposedCommandTest {

	private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	@Test
	public void testWriteCommand()  {
		AbstractCommand foo = mock(AbstractCommand.class);
		AbstractCommand bar = mock(AbstractCommand.class);
		ComposedCommand cmd = new ComposedCommand(foo, bar);
		cmd.writeCommand(mock(IPrologTermOutput.class));
		verify(foo).writeCommand(any(IPrologTermOutput.class));
		verify(bar).writeCommand(any(IPrologTermOutput.class));
	}

	@Test
	public void testWriteCommandAlternateConstructor()  {
		AbstractCommand foo = mock(AbstractCommand.class);
		AbstractCommand bar = mock(AbstractCommand.class);
		List<AbstractCommand> list = new ArrayList<>();
		list.add(foo);
		list.add(bar);
		ComposedCommand cmd = new ComposedCommand(list);
		cmd.writeCommand(mock(IPrologTermOutput.class));
		verify(foo).writeCommand(any(IPrologTermOutput.class));
		verify(bar).writeCommand(any(IPrologTermOutput.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testProcessResult()  {
		AbstractCommand foo = mock(AbstractCommand.class);
		AbstractCommand bar = mock(AbstractCommand.class);
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		ComposedCommand cmd = new ComposedCommand(foo, bar);
		cmd.processResult(map);
		verify(foo).processResult(any(ISimplifiedROMap.class));
		verify(bar).processResult(any(ISimplifiedROMap.class));
	}

	@Test
	public void testPrefix() {
		ComposedCommand command = new ComposedCommand();
		int i = 0;
		for (char c : LETTERS) {
			String prefix = command.createPrefix(i);
			assertEquals(c, prefix.charAt(0));
			assertEquals(1, prefix.length());
			i++;
		}
	}

	@Test
	public void testMorePrefix() {
		ComposedCommand command = new ComposedCommand();
		int i = LETTERS.length;
		for (char c : LETTERS) {
			String prefix = command.createPrefix(i);
			assertEquals(c, prefix.charAt(0));
			assertEquals('1', prefix.charAt(1));
			assertEquals(2, prefix.length());
			i++;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCommandNotKnown() {
		AbstractCommand foo = mock(AbstractCommand.class);
		AbstractCommand bar = mock(AbstractCommand.class);
		ComposedCommand cmd = new ComposedCommand(foo, bar);
		cmd.writeCommand(mock(IPrologTermOutput.class));
		verify(foo).writeCommand(any(IPrologTermOutput.class));
		verify(bar).writeCommand(any(IPrologTermOutput.class));
		
		AbstractCommand baz = mock(AbstractCommand.class);
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		cmd.getResultForCommand(baz, map);
	}
}
