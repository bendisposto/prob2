package de.prob.worksheet.api.evalStore;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.statespace.StateSpace;
import de.prob.worksheet.api.IContext;

/**
 * @author Rene
 * @see IContext
 */
public class EvalStoreContext implements IContext {
	private static Logger logger = LoggerFactory
			.getLogger(EvalStoreContext.class);
	String blockId;
	Long evalStoreId;
	StateSpace stateSpace;

	public EvalStoreContext(String id, Long evalStoreId, StateSpace statespace) {
		logger.trace("{} {}", blockId, evalStoreId);
		this.blockId = id;
		this.setBinding("StateSpace", statespace);
		this.setBinding("EvalStoreId", evalStoreId);
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
		ret.put("StateSpace", stateSpace);
		return ret;
	}

	@Override
	public Object getBinding(String name) {
		logger.trace(name);
		if (name.equals("EvalStoreId"))
			return this.evalStoreId;
		if (name.equals("StateSpace"))
			return this.stateSpace;
		logger.error("Request for an unknown binding");
		return null;
	}

	@Override
	public String toString() {
		return blockId + ":" + evalStoreId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EvalStoreContext) {
			HashMap<String, Object> objA = this.getBindings();
			HashMap<String, Object> objB = ((EvalStoreContext) obj)
					.getBindings();
			boolean a = objA.equals(objB);
			boolean b = this.blockId.equals(((EvalStoreContext) obj).getId());
			return a && b;
		}
		return false;
	}

	@Override
	public boolean equalsBindings(Object obj) {
		HashMap<String, Object> objA = this.getBindings();
		HashMap<String, Object> objB = ((EvalStoreContext) obj).getBindings();
		boolean a = objA.equals(objB);
		return a;
	}

	@Override
	public void setId(String id) {
		this.blockId = id;
	}

	@Override
	public void setBindings(HashMap<String, Object> bindings) {
		if (bindings.containsKey("EvalStoreId")) {
			if (!(bindings.get("EvalStoreId") instanceof Long))
				throw new IllegalArgumentException();
			this.evalStoreId = (Long) bindings.get("EvalStoreId");
		} else {
			this.evalStoreId = null;
		}
		if (bindings.containsKey("StateSpace")) {
			if (!(bindings.get("StateSpace") instanceof StateSpace))
				throw new IllegalArgumentException();
			this.stateSpace = (StateSpace) bindings.get("StateSpace");
		} else {
			this.stateSpace = null;
		}
	}

	@Override
	public void setBinding(String name, Object binding) {
		if (name == null)
			throw new IllegalArgumentException();

		assert ((name.equals("EvalStoreId") && (binding instanceof Long || binding == null)) || (name
				.equals("StateSpace") && (binding instanceof StateSpace || binding == null)));

		if (name.equals("EvalStoreId")) {
			this.evalStoreId = (Long) binding;
		}
		if (name.equals("StateSpace")) {
			this.stateSpace = (StateSpace) binding;
		}
	}

	@Override
	public IContext getDuplicate() {
		return new EvalStoreContext(this.getId(),
				(Long) this.getBinding("EvalStoreId"),
				(StateSpace) this.getBinding("StateSpace"));
	}

	@Override
	public void resetBindings() {
		this.evalStoreId = null;
		this.stateSpace = null;
	}
}
