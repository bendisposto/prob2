package de.bmotionstudio.core.editor.command;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;

public class ConnectionCreateCommand extends Command {

	private BConnection connection;
	private final BControl source;
	private BControl target;

	public ConnectionCreateCommand(BControl source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("connection creation");
		this.source = source;
	}

	public boolean canExecute() {
		// disallow source -> source connections
		if (source.equals(target)) {
			return false;
		}
		// return false, if the source -> target connection exists already
		for (Iterator<BConnection> iter = source.getSourceConnectionInstances()
				.iterator(); iter.hasNext();) {
			BConnection conn = iter.next();
			if (target != null && conn.getTarget().equals(target.getID())) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// create a new connection between source and target
		connection.setSource(source.getID());
		connection.setTarget(target.getID());
		connection.reconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		connection.reconnect();
	}

	/**
	 * Set the target endpoint for the connection.
	 * 
	 * @param target
	 *            that target endpoint (a non-null Shape instance)
	 * @throws IllegalArgumentException
	 *             if target is null
	 */
	public void setTarget(BControl target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		connection.disconnect();
	}

	public void setConnection(BConnection con) {
		this.connection = con;
	}

	public BConnection getConnection() {
		return this.connection;
	}

	public BControl getSource() {
		return this.source;
	}

	public BControl getTarget() {
		return this.target;
	}

}
