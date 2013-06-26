package de.prob.web;

import java.util.Map;
import java.util.UUID;

import com.google.common.util.concurrent.ListenableFuture;

public abstract class AbstractSession implements ISession {

	private UUID id;

	@Override
	abstract public ListenableFuture<Object> requestJson(
			Map<String, String[]> parameterMap);

	@Override
	public abstract String requestHtml(Map<String, String[]> parameterMap);

	@Override
	public void setUuid(UUID id) {
		this.id = id;

	}

	@Override
	public UUID getUuid() {
		return id;
	}

}
