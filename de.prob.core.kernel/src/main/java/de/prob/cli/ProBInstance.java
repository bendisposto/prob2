package de.prob.cli;

import com.google.inject.ImplementedBy;

import de.prob.ProBException;

@ImplementedBy(ProBInstanceImpl.class)
public interface ProBInstance {

	public abstract void shutdown();

	public abstract void sendUserInterruptReference(String home,
			OsSpecificInfo osInfo);

	public abstract String send(String term) throws ProBException;

	public boolean isShuttingDown();

}