package de.bmotionstudio.core.editor.command;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;

public class ConnectionReconnectCommand extends Command {

	/** The connection instance to reconnect. */
	private BConnection connection;
	/** The new source endpoint. */
	private BControl newSource;
	/** The new target endpoint. */
	private BControl newTarget;
	/** The original source endpoint. */
	private BControl oldSource;
	/** The original target endpoint. */
	private BControl oldTarget;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (newSource != null) {
			return checkSourceReconnection();
		} else if (newTarget != null) {
			return checkTargetReconnection();
		}
		return false;
	}

	/**
	 * Return true, if reconnecting the connection-instance to newSource is
	 * allowed.
	 */
	private boolean checkSourceReconnection() {
		// connection endpoints must be different Shapes
		if (newSource.equals(oldTarget)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator<BConnection> iter = newSource
				.getSourceConnectionInstances().iterator(); iter.hasNext();) {
			BConnection conn = (BConnection) iter.next();
			// return false if a newSource -> oldTarget connection exists
			// already
			// and it is a different instance than the connection-field
			if (conn.getTarget().equals(oldTarget) && !conn.equals(connection)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true, if reconnecting the connection-instance to newTarget is
	 * allowed.
	 */
	private boolean checkTargetReconnection() {
		// connection endpoints must be different Shapes
		if (newTarget.equals(oldSource)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator<BConnection> iter = newTarget.getTargetConnectionInstances()
				.iterator(); iter.hasNext();) {
			BConnection conn = (BConnection) iter.next();
			// return false if a oldSource -> newTarget connection exists
			// already
			// and it is a differenct instance that the connection-field
			if (conn.getSource().equals(oldSource) && !conn.equals(connection)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reconnect the connection to newSource (if setNewSource(...) was invoked
	 * before) or newTarget (if setNewTarget(...) was invoked before).
	 */
	public void execute() {
		if (newSource != null) {
			connection.reconnect(newSource.getID(), oldTarget.getID());
		} else if (newTarget != null) {
			connection.reconnect(oldSource.getID(), newTarget.getID());
		} else {
			throw new IllegalStateException("Should not happen");
		}
	}

	/**
	 * Set a new source endpoint for this connection. When execute() is invoked,
	 * the source endpoint of the connection will be attached to the supplied
	 * Shape instance.
	 * <p>
	 * Note: Calling this method, deactivates reconnection of the <i>target</i>
	 * endpoint. A single instance of this command can only reconnect either the
	 * source or the target endpoint.
	 * </p>
	 * 
	 * @param connectionSource
	 *            a non-null Shape instance, to be used as a new source endpoint
	 * @throws IllegalArgumentException
	 *             if connectionSource is null
	 */
	public void setNewSource(BControl connectionSource) {
		if (connectionSource == null) {
			throw new IllegalArgumentException();
		}
		setLabel("move connection startpoint");
		newSource = connectionSource;
		newTarget = null;
	}

	public BControl getNewSource() {
		return this.newSource;
	}

	/**
	 * Set a new target endpoint for this connection When execute() is invoked,
	 * the target endpoint of the connection will be attached to the supplied
	 * Shape instance.
	 * <p>
	 * Note: Calling this method, deactivates reconnection of the <i>source</i>
	 * endpoint. A single instance of this command can only reconnect either the
	 * source or the target endpoint.
	 * </p>
	 * 
	 * @param connectionTarget
	 *            a non-null Shape instance, to be used as a new target endpoint
	 * @throws IllegalArgumentException
	 *             if connectionTarget is null
	 */
	public void setNewTarget(BControl connectionTarget) {
		if (connectionTarget == null) {
			throw new IllegalArgumentException();
		}
		setLabel("move connection endpoint");
		newSource = null;
		newTarget = connectionTarget;
	}

	public BControl getNewTarget() {
		return this.newTarget;
	}

	/**
	 * Reconnect the connection to its original source and target endpoints.
	 */
	public void undo() {
		connection.reconnect(oldSource.getID(), oldTarget.getID());
	}

	public void setConnection(BConnection conn) {
		this.connection = conn;
		BControl sourceControl = conn.getVisualization().getBControl(
				conn.getSource());
		BControl targetControl = conn.getVisualization().getBControl(
				conn.getTarget());
		this.oldSource = sourceControl;
		this.oldTarget = targetControl;
	}

	public BConnection getConnection() {
		return this.connection;
	}

}
