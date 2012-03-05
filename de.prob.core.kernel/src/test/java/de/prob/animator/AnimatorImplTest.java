package de.prob.animator;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.animator.command.GetErrorsCommand;
import de.prob.animator.command.ICommand;
import de.prob.cli.ProBInstance;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;

public class AnimatorImplTest {

	private ICommand cmd;
	private ProBInstance cli;
	private CommandProcessor processor;
	private GetErrorsCommand getErrors;
	private AnimatorImpl animator;

	@Before
	public void prepareMocks() {
		cmd = mock(ICommand.class);
		cli = mock(ProBInstance.class);
		processor = new CommandProcessor();
		processor.configure(cli, LoggerFactory.getLogger(""));
		getErrors = mock(GetErrorsCommand.class);
		animator = new AnimatorImpl(cli, processor, getErrors);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandThrowsProBExceptionOnWrite() throws ProBException {
		doThrow(new ProBException()).when(cmd).writeCommand(
				any(IPrologTermOutput.class));
		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandThrowsProBExceptionOnProcess() throws ProBException {
		doThrow(new ProBException()).when(cmd).processResult(
				any(ISimplifiedROMap.class));
		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandThrowsRuntimeExceptionOnWrite() throws ProBException {

		doThrow(new NullPointerException()).when(cmd).writeCommand(
				any(IPrologTermOutput.class));

		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandThrowsRuntimeExceptionOnProcess()
			throws ProBException {
		doThrow(new NullPointerException()).when(cmd).processResult(
				any(ISimplifiedROMap.class));
		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testGetErrorsThrowsRuntimeExceptionOnWrite()
			throws ProBException {

		doThrow(new NullPointerException()).when(getErrors).writeCommand(
				any(IPrologTermOutput.class));

		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testGetErrorsThrowsRuntimeExceptionOnProcess()
			throws ProBException {
		doThrow(new NullPointerException()).when(getErrors).processResult(
				any(ISimplifiedROMap.class));
		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandsReturnsNullAsErrors() throws ProBException {
		when(getErrors.getErrors()).thenReturn(null);
		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandsHasSomeErrors() throws ProBException {
		when(getErrors.getErrors()).thenReturn(
				Arrays.asList(new String[] { "foo" }));
		animator.execute(cmd);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProBException.class)
	public void testCommandsHasNoErrors() throws ProBException {
		when(getErrors.getErrors()).thenReturn(Arrays.asList(new String[] {}));
		animator.execute(cmd);
	}

}
