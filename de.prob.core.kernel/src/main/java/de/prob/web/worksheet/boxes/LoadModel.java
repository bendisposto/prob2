package de.prob.web.worksheet.boxes;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.io.Files;

import de.prob.web.FileBrowserServlet;
import de.prob.worksheet.GroovySE;

public class LoadModel extends AbstractBox {

	private String content;

	public LoadModel() {
	}

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render() {
		File file = new File(content);
		if (!FileBrowserServlet.validProBFile(file))
			return pack(makeHtml(id,
					"<span class='illegal_file'> Not a valid ProB file. </span>"));
		else {
			String name = load_file(file.getAbsolutePath());
			return pack(makeHtml(id, "<b>" + content
					+ " has been loaded and stored in " + name + " </b>"));
		}
	}

	private String load_file(String filename) {
		ScriptEngine groovy = owner.groovy;
		String command = "";
		String name = "model_" + GroovySE.nextVar();
		String extension = Files.getFileExtension(filename);
		if (extension.equals("mch") || extension.equals("ref")
				|| extension.equals("imp")) {
			command = "api.b_load('" + filename + "')";
		}
		if (extension.equals("eventb")) {
			command = "api.eb_load('" + filename + "')";
		}
		if (extension.equals("csp")) {
			command = "api.csp_load('" + filename + "')";
		}
		if (command != "") {
			try {
				Object result = groovy.eval(command);
				Bindings bindings = groovy
						.getBindings(ScriptContext.GLOBAL_SCOPE);
				bindings.put(name, result);
				return name;
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		return "ERROR";
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	protected String getTemplate() {
		return "/ui/worksheet/load_model.html";
	}

	@Override
	protected boolean useCodemirror() {
		return false;
	}

}
