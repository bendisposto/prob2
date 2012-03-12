package de.prob.animator.command;

import static org.junit.Assert.*;

import org.junit.Test;

import test.TestHelper;
import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

public class SpecializedBooleanCheckersCommandTest {


	@Test
	public void testCheckInitialisationStatusCommandTrue() throws ProBException {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInitialisationStatusCommand command = new CheckInitialisationStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInitialized());
	}
	
	@Test
	public void testCheckInitialisationStatusCommandFalse() throws ProBException {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInitialisationStatusCommand command = new CheckInitialisationStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInitialized());
	}

	@Test
	public void testCheckInvariantStatusCommandTrue() throws ProBException {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInvariantStatusCommand command = new CheckInvariantStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInvariantViolated());
	}
	
	@Test
	public void testCheckInvariantStatusCommandFalse() throws ProBException {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInvariantStatusCommand command = new CheckInvariantStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInvariantViolated());
	}

	@Test
	public void testCheckMaxOperationReachedStatusCommandTrue()
			throws ProBException {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckMaxOperationReachedStatusCommand command = new CheckMaxOperationReachedStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.maxOperationReached());
	}
	
	@Test
	public void testCheckMaxOperationReachedStatusCommandFalse()
			throws ProBException {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckMaxOperationReachedStatusCommand command = new CheckMaxOperationReachedStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.maxOperationReached());
	}

	@Test
	public void testCheckTimeoutStatusCommandTrue() throws ProBException {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckTimeoutStatusCommand command = new CheckTimeoutStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isTimeout());
	}

	@Test
	public void testCheckTimeoutStatusCommandFalse() throws ProBException {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckTimeoutStatusCommand command = new CheckTimeoutStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isTimeout());
	}
}
