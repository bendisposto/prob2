package de.prob.cli;

import com.google.inject.ImplementedBy;

import de.prob.ProBException;

@ImplementedBy(ProBInstanceImpl.class)
public interface ProBInstance {

	public abstract void shutdown();

	public abstract void sendInterrupt();

	public abstract String send(String term) throws ProBException;

	public boolean isShuttingDown();

}