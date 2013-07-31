package de.prob.web.data;

import de.prob.web.ISession;

public class SessionResult {

	public final ISession session;
	public final Object result;

	public SessionResult(ISession session, Object result) {
		this.session = session;
		this.result = result;
	}

}
