package de.prob.worksheet.evaluator.evalStore;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.ContextHistory;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.IWorksheetEvent;
import de.prob.worksheet.api.WorksheetActionEvent;
import de.prob.worksheet.api.evalStore.EvalStoreContext;

public class HistoryListener implements IWorksheetAPIListener {
	Logger logger = LoggerFactory.getLogger(HistoryListener.class);
	ContextHistory contextHistory;
	public HistoryListener(ContextHistory contextHistory) {
		logger.trace("{}",contextHistory);
		this.contextHistory=contextHistory;
	}
	@Override
	public void notify(IWorksheetEvent event) {
		logger.trace(event.toString());
		if(event.getId()==2001){
			WorksheetActionEvent tEvent=(WorksheetActionEvent)event;
			this.contextHistory.add(new EvalStoreContext("", (Long) tEvent.getDataAfter()));
			logger.debug("{}",contextHistory);
		}
	}
}
