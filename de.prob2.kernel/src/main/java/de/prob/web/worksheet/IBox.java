package de.prob.web.worksheet;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.prob.web.views.Worksheet;

public interface IBox {

	void setId(String id);

	void setContent(Map<String, String[]> data);

	void setOwner(Worksheet owner);

	List<Object> render(BindingsSnapshot bindings);

	Map<String, String> createMessage();

	Map<String, String> replaceMessage();

	String getId();

	EChangeEffect changeEffect();

	boolean requiresReEvaluation();

	/*
	 * Returns additional commands following create Message for a box
	 */
	Collection<? extends Object> getMenuMessages();

	Object getAside(BindingsSnapshot previous_snapshot,
			BindingsSnapshot current_snapshot);

}