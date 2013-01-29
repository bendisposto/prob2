package de.prob.worksheet.api.evalStore;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.IContext;

public class EvalStoreContext implements IContext {
	Logger logger=LoggerFactory.getLogger(EvalStoreContext.class);
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
		//if(obj instanceof EvalStoreContext){
			HashMap<String, Object> objA=this.getBindings();
			HashMap<String, Object> objB=((EvalStoreContext) obj).getBindings();
			boolean a=objA.equals(objB);
			//boolean b=this.blockId.equals(((EvalStoreContext) obj).getID());
			return  a ;
		//}
		//return false;
	}
	@Override
	public void setId(String id) {
		this.blockId=id;
	}
}
