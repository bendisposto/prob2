package de.prob.web;

import java.util.Map;
import java.util.UUID;

import com.google.common.util.concurrent.ListenableFuture;

public abstract class AbstractSession implements ISession {

	private UUID id;
	private SessionQueue queue;

	@Override
	abstract public ListenableFuture<Object> requestJson(
			Map<String, String[]> parameterMap);

	@Override
	public abstract String requestHtml(Map<String, String[]> parameterMap);

	@Override
	public void setUuid(UUID id) {
		if (this.id != null)
			throw new IllegalStateException("Cannot set UUID multiple times");
		this.id = id;
	}

	@Override
	public UUID getUuid() {
		return id;
	}

	@Override
	public void setQueue(SessionQueue queue) {
		if (this.queue != null)
			throw new IllegalStateException("Cannot set Queue multiple times");
		this.queue = queue;
	}

	@Override
	public SessionQueue getQueue() {
		return queue;
	}

}
