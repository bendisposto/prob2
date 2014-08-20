/**
 * 
 */
package de.prob.cli;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts the reference for user interrupt calls from the process' startup
 * information. The reference must be later passed to the send_interrupt command
 * when an user interrupt should be signalled.
 * 
 * @author plagge
 */
class InterruptRefPattern extends AbstractCliPattern<Long> {

	private Long reference;

	private final Logger logger = LoggerFactory
			.getLogger(InterruptRefPattern.class);

	public InterruptRefPattern() {
		super("user interrupt reference id: *(\\d+) *$");
	}

	@Override
	protected void setValue(final Matcher matcher) {
		reference = Long.parseLong(matcher.group(1));
		logger.info("Server can receive user interrupts via reference "
				+ reference);
	}

	@Override
	public Long getValue() {
		return reference;
	}

	@Override
	public void notifyNotFound() {
		logger.error("Cannot determine process/thread ID of the Prolog core");

	}

	@Override
	public boolean notFoundIsFatal() {
		return true;
	}

}
