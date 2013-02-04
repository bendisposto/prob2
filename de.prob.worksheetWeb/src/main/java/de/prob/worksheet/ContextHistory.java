package de.prob.worksheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The ContextHistory stores a list of IContext objects in the order they are added or merged 
 * @author Rene
 */
public class ContextHistory implements Iterable<IContext> {
	Logger logger=LoggerFactory.getLogger(ContextHistory.class);
	private ArrayList<IContext> history;
	
	public ContextHistory(IContext initialContext) {
		logger.trace("{}",initialContext);
		if(initialContext==null  )
			throw new IllegalArgumentException();
		history=new ArrayList<IContext>();
		history.add(initialContext);
	}
	
	
	/*
	 * Returns the last Context before the first context with this id;
	 * IDs can be contained more than once in the History ()
	 */
	public IContext getInitialContextForId(String id){
		logger.trace("{}",id);
		logger.debug("{}",history);
		Iterator<IContext> it=history.iterator();
		IContext last=null;
		IContext next;
		while(it.hasNext()){
			next=it.next();
			if(next.getId().equals(id)){
				logger.trace("{}",last);
				return last;
			}
			last=next;
		}
		return last;
	}
	public void setContextsForId(String id,ContextHistory contextHistory){
		logger.trace("{}",id);
		logger.trace("{}",contextHistory);
		
		ListIterator<IContext> it=history.listIterator();
		int index=-1;
		int x=0;
		IContext next;
		while(it.hasNext()){
			next=it.next();
			if(next.getId().equals(id)){
				next.destroy();
				it.remove();
			}
			if(index==-1){
				if(next.equals(contextHistory.get(0)))
					index=x+1;
				else
					x++;
			}
		}
		logger.debug("{}",history);
		
		if(index==-1)
			index=history.size();
		boolean first=true;
		for(IContext context:contextHistory){
			if(first){
				first=false;
				continue;
			}
			context.setId(id);
			this.add(index, context);
			index++;
		}
		logger.trace("{}",history);
	}



	public IContext get(int index) {
		return this.history.get(index);
	}


	public int size() {
		logger.trace("{}",history.size());
		return history.size();
	}


	@Override
	public Iterator<IContext> iterator() {
		return this.history.iterator();
	}


	public IContext last() {
		logger.trace("",history.get(history.size()-1));
		logger.debug("{}",history);
		return history.get(history.size()-1);
	}


	public void add(IContext context) {
		logger.trace("{}",context);
		boolean equals=context.equals(last());
		logger.debug("{}",equals);
		if(equals){
			return;
		}
		this.history.add(context);
		logger.debug("{}",history);
	}
	
	

	public void add(int index, IContext context) {
		logger.trace("{}",context);
		if(index<history.size()){
			boolean equals=context.equalsBindings(history.get(index)) ;
			logger.debug("{}",equals);
			if(equals){
				history.get(index).setId(context.getId());
				return;
			}
		}
		this.history.add(index,context);
		
	}

	@Override
	public String toString() {
		return history.toString();
	}


	public ArrayList<IContext> getHistory() {
		return history;
	}


	public void removeHistoryAfterInitial(String id){
		logger.trace("{}",id);
		logger.debug("{}",history);
		Iterator<IContext> it=history.iterator();
		IContext next;
		boolean found=false;
		while(it.hasNext()){
			next=it.next();
			if(next.getId().equals(id)){
				found=true;
			}
			if(found){
				next.destroy();
				it.remove();
			}
		}
		return ;
	}

	
}
