package de.prob.worksheet.api.evalStore;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.IContext;

/**
 * @author Rene
 * @see IContext
 */
public class EvalStoreContext implements IContext {
	private static Logger logger=LoggerFactory.getLogger(EvalStoreContext.class);
	String blockId;
	Long evalStoreId;
	public EvalStoreContext(String blockId,Long evalStoreId) {
		logger.trace("{} {}",blockId,evalStoreId);
		this.blockId=blockId;
		this.evalStoreId=evalStoreId;
	}
	@Override
	public String getId() {
		logger.trace(this.blockId);
		return this.blockId;
	}

	@Override
	public HashMap<String, Object> getBindings() {
		logger.trace("");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("EvalStoreId", evalStoreId);
		return ret;
	}

	@Override
	public Object getBinding(String name) {
		logger.trace(name);
		if(name.equals("EvalStoreId")){
			logger.trace("{}",evalStoreId);
			return evalStoreId;	
		}
		logger.warn("Request for an unknown binding");
		return null;
	}
	@Override
	public void destroy() {
		logger.trace("");
		// TODO Ask Daniel for cleanup commands and add them
		
	}
	@Override
	public String toString() {
		return blockId+":"+evalStoreId;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EvalStoreContext){
			HashMap<String, Object> objA=this.getBindings();
			HashMap<String, Object> objB=((EvalStoreContext) obj).getBindings();
			boolean a=objA.equals(objB);
			boolean b=this.blockId.equals(((EvalStoreContext) obj).getId());
			return  a && b;
		}
		return false;
	}
	
	@Override
	public boolean equalsBindings(Object obj){
		HashMap<String, Object> objA=this.getBindings();
		HashMap<String, Object> objB=((EvalStoreContext) obj).getBindings();
		boolean a=objA.equals(objB);
		return  a ;
	}
	@Override
	public void setId(String id) {
		this.blockId=id;
	}
	@Override
	public void setBindings(HashMap<String, Object> bindings) {
		if(bindings.containsKey("EvalStoreId")){
			if(!(bindings.get("EvalStoreId") instanceof Long))
				throw new IllegalArgumentException();
			this.evalStoreId=(Long) bindings.get("EvalStoreId");
		}else{
			this.evalStoreId=null;	
		}
	}
	@Override
	public void setBinding(String name, Object binding) {
		if(!name.equals("EvalStoreId") || !(binding instanceof Long))
			throw new IllegalArgumentException();
		this.evalStoreId=(Long) binding;
	}
}
