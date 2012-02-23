package de.prob.animator.command;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.prolog.output.IPrologTermOutput;

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
}
