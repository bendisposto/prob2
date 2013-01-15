package de.prob.worksheet.api.state;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.WorksheetActionEvent;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.api.WorksheetOutputEvent;

public class StateAPI {
	// Error IDs
	public static final int						ERR_VAR_UNKNOWN_ID			= 1001;
	public static final String					ERR_VAR_UNKNOWN_NAME		= "var_unknown";
	public static final String					ERR_VAR_UNKNOWN_DESC		= "You have tried to access a variable of the state which doesn't exist";
	public static final String					ERR_VAR_UNKNOWN_MSG			= "Variable unknown";

	public static final int						ERR_VAR_ALREADY_EXISTS_ID	= 1001;
	public static final String					ERR_VAR_ALREADY_EXISTS_NAME	= "var_exists";
	public static final String					ERR_VAR_ALREADY_EXISTS_DESC	= "You have tried to add a variable which already exists in the state";
	public static final String					ERR_VAR_ALREADY_EXISTS_MSG	= "Variable exists";

	// Action IDs
	public static final int						ACTION_STATE_CHANGED_ID		= 2001;
	public static final String					ACTION_STATE_CHANGED_NAME	= "state_change";
	public static final String					ACTION_STATE_CHANGED_DESC	= "The state has changed";
	public static final String					ACTION_STATE_CHANGED_MSG	= "State changed";

	// Output IDs
	public static final int						OUTPUT_STATE_ID				= 2001;
	public static final String					OUTPUT_STATE_NAME			= "state_change";
	public static final String					OUTPUT_STATE_DESC			= "The state has changed";
	public static final String					OUTPUT_STATE_MSG			= "State changed";

	// private final Injector INJECTOR;
	private final List<IWorksheetAPIListener>	actionListeners;
	private final List<IWorksheetAPIListener>	outputListeners;
	private final List<IWorksheetAPIListener>	errorListeners;

	private State								state;

	@Inject
	public StateAPI() {
		// INJECTOR=ServletContextListener.INJECTOR;
		this.errorListeners = new ArrayList<IWorksheetAPIListener>();
		this.outputListeners = new ArrayList<IWorksheetAPIListener>();
		this.actionListeners = new ArrayList<IWorksheetAPIListener>();
	}

	public void getCurrentState() {
		// TODO add calls to ProB2
		this.state = new State();
		this.state.variables.put("waiting", "{}");
		this.state.variables.put("running", "{PID1}");
		this.state.variables.put("blocked", "{PID3,PID2}");
	}

	public void removeVariable(final String varName) {
		if (!this.state.hasVariable(varName)) {
			this.notifyErrorListeners(StateAPI.ERR_VAR_UNKNOWN_ID, StateAPI.ERR_VAR_UNKNOWN_NAME, StateAPI.ERR_VAR_UNKNOWN_DESC, StateAPI.ERR_VAR_UNKNOWN_MSG, true);
			return;
		}
		final State oldState = this.state.clone();
		this.state.removeVariable(varName);
		this.notifyActionListeners(StateAPI.ACTION_STATE_CHANGED_ID, StateAPI.ACTION_STATE_CHANGED_NAME, StateAPI.ACTION_STATE_CHANGED_DESC, StateAPI.ACTION_STATE_CHANGED_MSG, oldState, this.state);
		this.notifyOutputListeners(StateAPI.OUTPUT_STATE_ID, StateAPI.OUTPUT_STATE_NAME, StateAPI.OUTPUT_STATE_DESC, StateAPI.OUTPUT_STATE_MSG, "Text", this.state);
	}

	public void addVariable(final String varName, final String value) {
		if (this.state.hasVariable(varName)) {
			this.notifyErrorListeners(StateAPI.ERR_VAR_ALREADY_EXISTS_ID, StateAPI.ERR_VAR_ALREADY_EXISTS_NAME, StateAPI.ERR_VAR_ALREADY_EXISTS_DESC, StateAPI.ERR_VAR_ALREADY_EXISTS_MSG, true);
			return;
		}
		final State oldState = this.state.clone();
		this.state.addVariable(varName, value);
		this.notifyActionListeners(StateAPI.ACTION_STATE_CHANGED_ID, StateAPI.ACTION_STATE_CHANGED_NAME, StateAPI.ACTION_STATE_CHANGED_DESC, StateAPI.ACTION_STATE_CHANGED_MSG, oldState, this.state);
		this.notifyOutputListeners(StateAPI.OUTPUT_STATE_ID, StateAPI.OUTPUT_STATE_NAME, StateAPI.OUTPUT_STATE_DESC, StateAPI.OUTPUT_STATE_MSG, "Text", this.state);
	}

	public void notifyErrorListeners(final int id, final String name, final String description, final String message, final boolean haltAll) {
		final WorksheetErrorEvent event = new WorksheetErrorEvent(id,message,haltAll);
		event.setDescription(description);
		event.setName(name);
		for (final IWorksheetAPIListener listener : this.errorListeners) {
			listener.notify(event);
		}
	}

	public void addErrorListener(final IWorksheetAPIListener listener) {
		assert (this.errorListeners != null);
		assert (listener != null);
		if (!this.errorListeners.contains(listener)) {
			this.errorListeners.add(listener);
		}

	}

	public void removeErrorListener(final IWorksheetAPIListener listener) {
		assert (this.errorListeners != null);
		this.errorListeners.remove(listener);
	}

	public void notifyOutputListeners(final int id, final String name, final String description, final String message, final String outputBlockType, final Object dataObject) {
		final WorksheetOutputEvent event = new WorksheetOutputEvent();
		event.setId(id);
		event.setName(name);
		event.setDescription(description);
		event.setOutputBlockType(outputBlockType);
		event.setMessage(message);
		event.setDataObject(dataObject);
		for (final IWorksheetAPIListener listener : this.outputListeners) {
			listener.notify(event);
		}
	}

	public void addOutputListener(final IWorksheetAPIListener listener) {
		assert (this.outputListeners != null);
		assert (this.outputListeners != null);
		if (!this.outputListeners.contains(listener)) {
			this.outputListeners.add(listener);
		}
	}

	public void removeOutputListener(final IWorksheetAPIListener listener) {
		this.outputListeners.remove(listener);
	}

	public void notifyActionListeners(final int id, final String name, final String description, final String message, final Object dataBefore, final Object dataAfter) {
		final WorksheetActionEvent event = new WorksheetActionEvent();
		event.setId(id);
		event.setName(name);
		event.setDescription(description);
		event.setMessage(message);
		event.setDataBefore(dataBefore);
		event.setDataAfter(dataAfter);
		for (final IWorksheetAPIListener listener : this.actionListeners) {
			listener.notify(event);
		}
	}

	public void addActionListener(final IWorksheetAPIListener listener) {
		assert (this.actionListeners != null);
		assert (this.actionListeners != null);

		this.actionListeners.add(listener);

	}

	public void removeActionListener(final IWorksheetAPIListener listener) {
		this.actionListeners.remove(listener);
	}

}
