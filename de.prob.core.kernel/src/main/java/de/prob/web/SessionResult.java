package de.prob.web;

public class SessionResult {

	public final ISession session;
	public final Object result;

	public SessionResult(ISession session, Object result) {
		this.session = session;
		this.result = result;
	}

}
