package de.prob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProBException extends Exception {

	private final static Logger logger = LoggerFactory
			.getLogger(ProBException.class);

	public ProBException(Throwable e) {
		super(e);
	}

	public ProBException(String msg) {
		super(msg);
	}

	public ProBException() {
		ProBException.logger.warn("Missing details in Exception.");
	}

	private static final long serialVersionUID = -1125686053595934960L;
}
