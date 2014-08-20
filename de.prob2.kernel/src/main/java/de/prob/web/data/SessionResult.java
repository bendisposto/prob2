package de.prob.web.data;

import de.prob.web.ISession;

public class SessionResult {

	public final ISession session;
	public final Object[] result;

	public SessionResult(ISession session, Object[] result) {
		this.session = session;
		if (result.length == 1 && result[0] == null) {
			this.result = new Object[0];
		} else {
			this.result = result;
		}
	}

	public SessionResult(ISession delegate, Object result) {
		this(delegate, new Object[] { result });
	}
}
