package de.prob.web.worksheet.renderer;

import java.util.List;
import java.util.Map;

import de.prob.web.views.Worksheet;

public interface IRender {

	String getTemplate();

	Map<String, String> getExtraInfo();

	String initialContent();

	boolean useCodemirror();

	List<Object> render(String id, String text, Worksheet worksheet);

}