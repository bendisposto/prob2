package de.prob.animator.command;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class ComposedCommandTest {

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
	public void testProcessResult() throws ProBException {
		ICommand foo = mock(ICommand.class);
		ICommand bar = mock(ICommand.class);
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		ComposedCommand cmd = new ComposedCommand(foo, bar);
		cmd.processResult(map);
		verify(foo).processResult(any(ISimplifiedROMap.class));
		verify(bar).processResult(any(ISimplifiedROMap.class));
	}
}
