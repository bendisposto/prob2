package de.prob.worksheet.api.evalStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.inject.Inject;
import com.google.inject.Injector;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.command.GetStateValuesCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;
import de.prob.worksheet.IContext;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.WorksheetActionEvent;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.api.WorksheetOutputEvent;
import de.prob.worksheet.evaluator.evalStore.ErrorListener;

public class EvalStoreAPI {
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

	private List<IWorksheetAPIListener>	actionListeners;
	private List<IWorksheetAPIListener>	outputListeners;
	private List<IWorksheetAPIListener>	errorListeners;

	private Long evalStoreId;
	private AnimationSelector animations;
	public static Injector injector = ServletContextListener.INJECTOR;
	
	Logger logger=LoggerFactory.getLogger(this.getClass());
	@Inject
	public EvalStoreAPI(AnimationSelector animations) {
		logger.trace("{}",animations.getHistories());
		this.animations=animations;
		// INJECTOR=ServletContextListener.INJECTOR;
		this.errorListeners = new ArrayList<IWorksheetAPIListener>();
		logger.debug("{}",errorListeners);
		this.outputListeners = new ArrayList<IWorksheetAPIListener>();
		logger.debug("{}",this.outputListeners);
		this.actionListeners = new ArrayList<IWorksheetAPIListener>();
		logger.debug("{}",actionListeners);
		//TODO remove when rodin ui works;
	}

	public void getCurrentState() {
		logger.trace("");
		Long before=this.evalStoreId;
		this.animations=injector.getInstance(AnimationSelector.class);
		logger.debug("Animations: " + animations.getHistories());
		History currentHistory = animations.getCurrentHistory();
		logger.debug("CurrentHistory: "+currentHistory);
		if(currentHistory==null){
			this.notifyErrorListeners(EvalStoreAPI.ERR_VAR_UNKNOWN_ID, "noHistory", "No Animation is started","No Animation is started", true);
			return;
		}
		StateId sId=currentHistory.getCurrentState();
		logger.debug("Current StateId"+sId.getId());
		
		EvalstoreCreateByStateCommand cmd=new EvalstoreCreateByStateCommand(sId.getId());
		currentHistory.getStatespace().execute(cmd);
		this.evalStoreId=cmd.getEvalstoreId();
		logger.debug("EvalstoreId: "+this.evalStoreId);
		GetStateValuesCommand valCmd=GetStateValuesCommand.getEvalstoreValuesCommand(this.evalStoreId);
		currentHistory.getStatespace().execute(valCmd);
		
		String output="";
		HashMap<String, String> values=valCmd.getResult();
		logger.debug("Current Store Values: "+values);
		
		Set<Entry<String,String>> entries = values.entrySet();
		for(Entry<String,String> value:entries){
			output+=value.getKey()+":="+value.getValue();
		}
		this.notifyActionListeners(EvalStoreAPI.ACTION_STATE_CHANGED_ID,EvalStoreAPI.ACTION_STATE_CHANGED_NAME,EvalStoreAPI.ACTION_STATE_CHANGED_DESC,EvalStoreAPI.ACTION_STATE_CHANGED_MSG,before,this.evalStoreId);
		this.notifyOutputListeners(EvalStoreAPI.OUTPUT_STATE_ID, EvalStoreAPI.OUTPUT_STATE_NAME, EvalStoreAPI.OUTPUT_STATE_DESC, "Current State:\n" , "html", output);
	}

	
	public void evaluate(String expression){
		logger.trace("{}",expression);
		Long before=this.evalStoreId;
		if(this.evalStoreId==null){
			this.notifyErrorListeners(EvalStoreAPI.ERR_VAR_UNKNOWN_ID, "", "", "no State is initialized (call getCurrentState)", true);
			return;
		}
		IEvalElement eval=new EventB(expression);
		EvalstoreEvalCommand cmd= new EvalstoreEvalCommand(this.evalStoreId, eval);
		this.animations.getCurrentHistory().getStatespace().execute(cmd);
		EvalstoreResult storeResult=cmd.getResult();
		if(storeResult.isSuccess()){
			this.evalStoreId=storeResult.getResultingStoreId();
			this.notifyActionListeners(EvalStoreAPI.ACTION_STATE_CHANGED_ID,EvalStoreAPI.ACTION_STATE_CHANGED_NAME,EvalStoreAPI.ACTION_STATE_CHANGED_DESC,EvalStoreAPI.ACTION_STATE_CHANGED_MSG,before,this.evalStoreId);
			logger.debug("{}",storeResult.getResult());
			this.notifyOutputListeners(ACTION_STATE_CHANGED_ID, "", "", "Result:\n", "html", storeResult.getResult().getValue());
			storeResult.getResult().getValue();
		}else{
			if(storeResult.hasInterruptedOccurred()){
				this.notifyErrorListeners(EvalStoreAPI.ERR_VAR_UNKNOWN_ID, "", "", "No Success Interrupt", true);		
				logger.error("{}",storeResult.getResult());
			}
			if(storeResult.hasTimeoutOccurred()){	
				this.notifyErrorListeners(EvalStoreAPI.ERR_VAR_UNKNOWN_ID, "", "", "No Success Timeout", true);		
				logger.error("{}",storeResult.getResult());
			}
			if(storeResult.getResult().hasError()){
				this.notifyErrorListeners(EvalStoreAPI.ERR_VAR_UNKNOWN_ID, "", "", "No Success Result Error: "+storeResult.getResult().getErrors(), true);		
				logger.error("{}",storeResult.getResult());
			}
		}
		
		
	}
	public void getStoreValues(){
		GetStateValuesCommand cmd=GetStateValuesCommand.getEvalstoreValuesCommand(this.evalStoreId);
		animations.getCurrentHistory().getStatespace().execute(cmd);
		notifyOutputListeners(OUTPUT_STATE_ID, "", "", "State:\n", "html", cmd.getResult().toString());
	}
	public void notifyErrorListeners(final int id, final String name, final String description, final String message, final boolean haltAll) {
		logger.trace("id{}",id);
		logger.trace("name{}",name);
		logger.trace("description{}",description);
		logger.trace("haltAll{}",haltAll);
		
		final WorksheetErrorEvent event = new WorksheetErrorEvent(id,message,haltAll);
		event.setDescription(description);
		event.setName(name);
		for (final IWorksheetAPIListener listener : this.errorListeners) {
			listener.notify(event);
		}
	}

	public void addErrorListener(final IWorksheetAPIListener listener) {
		logger.trace("{}",listener);
		assert (this.errorListeners != null);
		assert (listener != null);
		
		if (!this.errorListeners.contains(listener)) {
			this.errorListeners.add(listener);
			logger.debug("listeners:{}",this.errorListeners);
		}
		
	}

	public void removeErrorListener(final IWorksheetAPIListener listener) {
		logger.trace("{}",listener);
		assert (this.errorListeners != null);
		this.errorListeners.remove(listener);
		logger.debug("{}",errorListeners);
	}

	public void notifyOutputListeners(final int id, final String name, final String description, final String message, final String outputBlockType, final Object dataObject) {
		logger.trace("id{}",id);
		logger.trace("name{}",name);
		logger.trace("description{}",description);
		logger.trace("message{}",message);
		logger.trace("data{}",dataObject);
		
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
		logger.trace("{}",listener);
		assert (this.outputListeners != null);
		assert (this.outputListeners != null);
		if (!this.outputListeners.contains(listener)) {
			this.outputListeners.add(listener);
			logger.debug("{}",outputListeners);
		}
	}

	public void removeOutputListener(final IWorksheetAPIListener listener) {
		logger.trace("{}",listener);
		this.outputListeners.remove(listener);
		logger.debug("{}",outputListeners);
	}

	public void notifyActionListeners(final int id, final String name, final String description, final String message, final Object dataBefore, final Object dataAfter) {
		logger.trace("id{}",id);
		logger.trace("name{}",name);
		logger.trace("description{}",description);
		logger.trace("message{}",message);
		logger.trace("before{}",dataBefore);
		logger.trace("after{}",dataAfter);
	
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
		logger.trace("{}",listener);
		assert (this.actionListeners != null);
		assert (this.actionListeners != null);

		this.actionListeners.add(listener);
		logger.debug("{}",actionListeners);
	}

	public void removeActionListener(final IWorksheetAPIListener listener) {
		logger.trace("{}",listener);
		this.actionListeners.remove(listener);
		logger.debug("{}",actionListeners);
	}

	public void setEvalStoreId(Long id) {
		logger.trace("{}",id);
		this.evalStoreId=id;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
