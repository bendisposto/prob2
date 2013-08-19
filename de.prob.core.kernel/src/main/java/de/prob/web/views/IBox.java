package de.prob.web.views;

import java.util.List;
import java.util.Map;

public interface IBox {

	void setId(String id);

	void setContent(Map<String, String[]> data);

	void setOwner(Worksheet owner);

	List<Object> render();

	Map<String, String> createMessage();

	Map<String, String> replaceMessage();

	String getId();

}