package de.prob.web.views;

import java.util.List;
import java.util.Map;

import de.prob.web.worksheet.EChangeEffect;

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

}