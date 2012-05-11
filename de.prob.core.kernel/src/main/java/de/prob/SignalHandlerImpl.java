package de.prob;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import de.prob.statespace.StateSpace;

public class SignalHandlerImpl implements SignalHandler {

	@SuppressWarnings("unused")
	private SignalHandler oldHandler;
	private final StateSpace s;

	public SignalHandlerImpl(StateSpace s) {
		this.s = s;
	}

	public static SignalHandler install(String signalName, final StateSpace s) {
		Signal diagSignal = new Signal(signalName);
		SignalHandlerImpl instance = new SignalHandlerImpl(s);
		instance.oldHandler = Signal.handle(diagSignal, instance);
		return instance;
	}

	@Override
	public void handle(Signal signal) {
		try {
			signalAction(signal);
		} catch (Exception e) {
			System.out.println("handle|Signal handler failed, reason "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void signalAction(Signal signal) {
		s.sendInterrupt();
	}
}
